package com.lumina_bank.paymentservice.config.event;

import com.lumina_bank.common.dto.event.payment_events.PaymentRiskResultEvent;
import com.lumina_bank.paymentservice.service.payment.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class PaymentEventsConsumerConfig {
    private final PaymentTransactionService paymentTransactionService;

    @Bean
    public Consumer<PaymentRiskResultEvent> paymentRiskResultConsumer(){
        return event->{
            if (event == null) {
                log.warn("Received null PaymentRiskResultEvent");
                return;
            }

            log.info("Received PaymentRiskResultEvent paymentId={}", event.paymentId());

            switch (event.riskLevel()){
                case HIGH -> paymentTransactionService.markBlocking(event.paymentId(),event.riskScore(),event.riskLevel());
                case MEDIUM ->  paymentTransactionService.markFlagged(event.paymentId(),event.riskScore(),event.riskLevel());
                case LOW -> paymentTransactionService.markPending(event.paymentId());
            }
        };
    }
}
