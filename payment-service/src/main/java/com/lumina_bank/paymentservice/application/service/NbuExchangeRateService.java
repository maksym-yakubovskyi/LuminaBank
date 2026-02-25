package com.lumina_bank.paymentservice.application.service;

import com.lumina_bank.common.enums.payment.Currency;
import com.lumina_bank.common.exception.ExternalServiceException;
import com.lumina_bank.paymentservice.infrastructure.external.NBU.dto.ExchangeRateResponse;
import com.lumina_bank.paymentservice.domain.exception.ExchangeRateNotFoundException;
import com.lumina_bank.paymentservice.domain.model.ExchangeRate;
import com.lumina_bank.paymentservice.domain.repository.ExchangeRateRepository;
import com.lumina_bank.paymentservice.infrastructure.external.NBU.NbuFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NbuExchangeRateService {
    private static final int SCALE = 6;

    private final NbuFeignClient nbuFeignClient;
    private final ExchangeRateRepository exchangeRateRepository;

    public void fetchAndStoreRates() {
        log.info("Fetching exchange rates from NBU...");

        List<ExchangeRateResponse> rates;
        try {
            rates = nbuFeignClient.getRates();
        } catch (Exception e) {
            log.warn("Failed to fetch exchange rates from NBU API: {}", e.getMessage());
            throw new ExternalServiceException("Failed to fetch exchange rates from NBU API", e);
        }
        if (rates == null || rates.isEmpty()) {
            log.warn("NBU API returned empty exchange rate list");
            return;
        }
        log.info("Received {} exchange rates from NBU", rates.size());

        LocalDateTime now = LocalDateTime.now();

        for (ExchangeRateResponse rate : rates) {
            try {
                Currency targetCurrency = Currency.valueOf(rate.cc());

                ExchangeRate entity = ExchangeRate.builder()
                        .baseCurrency(Currency.UAH)
                        .targetCurrency(targetCurrency)
                        .rate(rate.rate())
                        .date(now)
                        .build();

                exchangeRateRepository.save(entity);

                log.debug("Saved rate: 1 {} = {} {}",
                        entity.getBaseCurrency(),
                        entity.getRate(),
                        entity.getTargetCurrency());

            } catch (IllegalArgumentException e) {
                log.warn("Unknown currency code from NBU API: {}", rate.cc());
            } catch (Exception e) {
                log.error("Error saving exchange rate for {}", rate.cc(), e);
            }
        }

        log.info("Exchange rates successfully updated in database");
    }

    @Transactional(readOnly = true)
    public BigDecimal getRate(Currency from, Currency to) {
        // Якщо валюти однакові → UAH = UAH
        if (from.equals(to)) return BigDecimal.ONE;

        try {
            // Якщо to = UAH → шукаємо курс з UAH до from
            if (to.equals(Currency.UAH)) {
                return findRateToUAH(from);
            }

            // Якщо from = UAH → шукаємо курс з UAH до to й інвертуємо
            if (from.equals(Currency.UAH)) {
                return invert(findRateToUAH(to));
            }

            // Якщо обидві не гривня (наприклад USD → EUR)
            BigDecimal fromToUAH = findRateToUAH(from);
            BigDecimal toToUAH = findRateToUAH(to);

            BigDecimal result  = toToUAH.divide(fromToUAH, SCALE, RoundingMode.HALF_UP);

            log.debug("Calculated indirect rate from {} to {} = {}", from, to, result);
            return result;
        } catch (ExchangeRateNotFoundException e) {
            log.warn(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.warn("Unexpected error while fetching rate from {} to {}: {}", from, to, e.getMessage(), e);
            throw new ExternalServiceException("Failed to retrieve exchange rate", e);
        }
    }

    private BigDecimal findRateToUAH(Currency currency) {
        return exchangeRateRepository
                .findTopByBaseCurrencyAndTargetCurrencyOrderByDateDesc(Currency.UAH, currency)
                .map(ExchangeRate::getRate)
                .orElseThrow(() ->
                        new ExchangeRateNotFoundException("No exchange rate found for UAH → " + currency));
    }

    private BigDecimal invert(BigDecimal rate) {
        return BigDecimal.ONE.divide(rate, SCALE, RoundingMode.HALF_UP);
    }
}