package com.lumina_bank.analyticsservice.infrastructure.config;

import com.lumina_bank.analyticsservice.application.analytics.AnalyticsEventProcessor;
import com.lumina_bank.common.dto.event.payment_events.PaymentBlockedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentCompletedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentFlaggedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AnalyticsEventsConsumerConfig {
    private final AnalyticsEventProcessor eventProcessor;

    @Bean
    public Consumer<PaymentCompletedEvent> paymentCompletedConsumer() {
        return event -> {
            if (event == null) {
                log.warn("Received null PaymentCompletedEvent");
                return;
            }
            log.info("Received PaymentCompletedEvent");
            eventProcessor.handlePaymentCompleted(event);

        };
    }

    @Bean
    public Consumer<PaymentFlaggedEvent> paymentFlaggedConsumer() {
        return event -> {
            if (event == null) {
                log.warn("Received null PaymentFlaggedEvent");
                return;
            }
            log.info("Received PaymentFlaggedEvent");
            eventProcessor.handlePaymentFlagged(event);
        };
    }

    @Bean
    public Consumer<PaymentBlockedEvent> paymentBlockedConsumer() {
        return event -> {
            if (event == null) {
                log.warn("Received null PaymentBlockedEvent");
                return;
            }
            log.info("Received PaymentBlockedEvent");
            eventProcessor.handlePaymentBlocked(event);
        };
    }
}
