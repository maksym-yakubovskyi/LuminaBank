package com.lumina_bank.frauddetectionservice.infrastructure.messaging.consumer;

import com.lumina_bank.common.dto.event.payment_events.PaymentCreatedEvent;
import com.lumina_bank.frauddetectionservice.application.service.ActivityService;
import com.lumina_bank.frauddetectionservice.application.service.FraudDetectionService;
import com.lumina_bank.frauddetectionservice.application.service.ProfileService;
import com.lumina_bank.frauddetectionservice.application.service.RecipienceService;
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