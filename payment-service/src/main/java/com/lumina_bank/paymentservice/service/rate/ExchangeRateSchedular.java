package com.lumina_bank.paymentservice.service.rate;

import com.lumina_bank.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateSchedular {

    private final NbuExchangeRateService nbuExchangeRateService;

    @Scheduled(cron = "0 0 10 * * *")
    public void updateRates() {
        log.info("Starting daily exchange rate update from NBU at {}", LocalDateTime.now());

        try {
            nbuExchangeRateService.fetchAndStoreRates();

            log.info("Exchange rate update completed successfully at {}", LocalDateTime.now());
        } catch (BusinessException e) {
            log.error("Failed to fetch exchange rates from NBU service: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during NBU exchange rate update: {}", e.getMessage());
        }
    }
}