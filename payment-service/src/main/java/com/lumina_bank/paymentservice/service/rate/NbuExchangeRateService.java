package com.lumina_bank.paymentservice.service.rate;

import com.lumina_bank.paymentservice.dto.ExchangeRateResponse;
import com.lumina_bank.paymentservice.enums.Currency;
import com.lumina_bank.paymentservice.exception.ExchangeRateNotFoundException;
import com.lumina_bank.paymentservice.exception.ExternalServiceException;
import com.lumina_bank.paymentservice.model.ExchangeRate;
import com.lumina_bank.paymentservice.repository.ExchangeRateRepository;
import com.lumina_bank.paymentservice.service.client.NbuFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    private final NbuFeignClient nbuFeignClient;
    private final ExchangeRateRepository exchangeRateRepository;

    @Transactional
    public void fetchAndStoreRates() {
        log.info("Fetching exchange rates from NBU...");

        ResponseEntity<List<ExchangeRateResponse>> response;
        try {
            response = nbuFeignClient.getRates();
        } catch (Exception e) {
            log.warn("Failed to fetch exchange rates from NBU API: {}", e.getMessage());
            throw new ExternalServiceException("Failed to fetch exchange rates from NBU API", e);
        }

        if (response == null) {
            log.warn("Null response from NBU API");
            throw new ExternalServiceException("Null response from NBU API");
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.warn("NBU API returned non-2xx status: {}, body={}",
                    response.getStatusCode(), response.getBody());
            throw new ExternalServiceException("NBU API returned non-2xx status: " + response.getStatusCode());
        }

        List<ExchangeRateResponse> rates = response.getBody();
        if (rates == null || rates.isEmpty()) {
            log.warn("NBU API returned empty exchange rate list");
            return;
        }

        log.info("Received {} exchange rates from NBU", rates.size());

        rates.forEach(rate -> {
            try {
                Currency targetCurrency = Currency.valueOf(rate.cc());

                ExchangeRate entity = ExchangeRate.builder()
                        .baseCurrency(Currency.UAH)
                        .targetCurrency(targetCurrency)
                        .rate(rate.rate())
                        .date(LocalDateTime.now())
                        .build();

                exchangeRateRepository.save(entity);

                log.debug("Saved rate: 1 {} = {} {}",
                        entity.getBaseCurrency(), entity.getRate(), entity.getTargetCurrency());

            } catch (IllegalArgumentException e) {
                log.warn("Unknown currency code from NBU API: {}", rate.cc());
            } catch (Exception e) {
                log.warn("Error saving exchange rate for {}: {}", rate.cc(), e.getMessage());
            }
        });

        log.info("Exchange rates successfully updated in database");
    }

    @Transactional
    public BigDecimal getRate(Currency from, Currency to) {
        // Якщо валюти однакові → UAH = UAH
        if (from.equals(to)) return BigDecimal.ONE;

        try {
            // Якщо to = UAH → шукаємо курс з UAH до from
            if (to.equals(Currency.UAH)) {
                return exchangeRateRepository
                        .findTopByBaseCurrencyAndTargetCurrencyOrderByDateDesc(Currency.UAH, from)
                        .map(ExchangeRate::getRate)
                        .orElseThrow(() -> new ExchangeRateNotFoundException("No exchange rate found for " + from + " to UAH"));
            }

            // Якщо from = UAH → шукаємо курс з UAH до to й інвертуємо
            if (from.equals(Currency.UAH)) {
                return exchangeRateRepository
                        .findTopByBaseCurrencyAndTargetCurrencyOrderByDateDesc(Currency.UAH, to)
                        .map(rate -> BigDecimal.ONE.divide(rate.getRate(), 4, RoundingMode.HALF_UP))
                        .orElseThrow(() -> new ExchangeRateNotFoundException("No exchange rate found for UAH to " + to));
            }

            // Якщо обидві не гривня (наприклад USD → EUR)
            BigDecimal fromToUAH = exchangeRateRepository
                    .findTopByBaseCurrencyAndTargetCurrencyOrderByDateDesc(Currency.UAH, from)
                    .map(ExchangeRate::getRate)
                    .orElseThrow(() -> new ExchangeRateNotFoundException("No rate found for " + from + " → UAH"));

            BigDecimal toToUAH = exchangeRateRepository
                    .findTopByBaseCurrencyAndTargetCurrencyOrderByDateDesc(Currency.UAH, to)
                    .map(ExchangeRate::getRate)
                    .orElseThrow(() -> new ExchangeRateNotFoundException("No rate found for " + to + " → UAH"));

            BigDecimal rate = toToUAH.divide(fromToUAH, 4, RoundingMode.HALF_UP);
            log.debug("Calculated indirect rate from {} to {} = {}", from, to, rate);
            return rate;

        } catch (ExchangeRateNotFoundException e) {
            log.warn(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.warn("Unexpected error while fetching rate from {} to {}: {}", from, to, e.getMessage(), e);
            throw new ExternalServiceException("Failed to retrieve exchange rate", e);
        }
    }
}
