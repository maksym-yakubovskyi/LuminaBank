package com.lumina_bank.frauddetectionservice.config.event;

import com.lumina_bank.common.dto.event.payment_events.PaymentCreatedEvent;
import com.lumina_bank.frauddetectionservice.sevice.ActivityService;
import com.lumina_bank.frauddetectionservice.sevice.FraudDetectionService;
import com.lumina_bank.frauddetectionservice.sevice.ProfileService;
import com.lumina_bank.frauddetectionservice.sevice.RecipienceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class PaymentEventsConsumerConfig {
    private final FraudDetectionService service;

    private final ProfileService profileService;
    private final RecipienceService recipienceService;
    private final ActivityService activityService;

    @Bean
    public Consumer<PaymentCreatedEvent> paymentCreatedEventConsumer() {
        return event -> {
            if (event == null) {
                log.warn("Received null PaymentCreatedEvent");
                return;
            }

            log.info("Received PaymentCreatedEvent paymentId={}", event.paymentId());

            service.checkPayment(event);

            profileService.registerPaymentAttempt(event);
            recipienceService.registerRecipient(event);
            activityService.registerActivity(event);
        };
    }
}