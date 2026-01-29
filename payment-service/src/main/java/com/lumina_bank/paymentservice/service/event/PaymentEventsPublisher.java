package com.lumina_bank.paymentservice.service.event;

import com.lumina_bank.common.dto.event.payment_events.PaymentCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventsPublisher {
    private final StreamBridge streamBridge;

    public void publishPaymentCreated(PaymentCreatedEvent event){
        log.info("Publishing PaymentCreated event to stream");

        boolean sent = streamBridge.send("paymentCreated-out-0", event);

        if(sent){
            log.info("PaymentCreated event sent successfully, paymentId={}", event.paymentId());
        }else {
            log.warn("Failed to publish PaymentCreatedEvent, paymentId={}", event.paymentId());
        }
    }
}
