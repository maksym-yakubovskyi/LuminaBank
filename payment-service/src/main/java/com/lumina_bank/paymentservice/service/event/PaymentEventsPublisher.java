package com.lumina_bank.paymentservice.service.event;

import com.lumina_bank.common.dto.event.payment_events.PaymentBlockedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentCompletedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentCreatedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentFlaggedEvent;
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

    public void publishPaymentCompleted(PaymentCompletedEvent event){
        log.info("Publishing PaymentCompleted event to stream");

        boolean sent = streamBridge.send("paymentCompleted-out-0", event);

        if(sent){
            log.info("PaymentCompleted event sent successfully, paymentId={}", event.paymentId());
        }else {
            log.warn("Failed to publish PaymentCompletedEvent, paymentId={}", event.paymentId());
        }
    }

    public void publishPaymentFlagged(PaymentFlaggedEvent event){
        log.info("Publishing PaymentFlagged event to stream");
        boolean sent = streamBridge.send("paymentFlagged-out-0", event);
        if(sent){
            log.info("PaymentFlagged event sent successfully, paymentId={}", event.paymentId());
        }else{
            log.warn("Failed to publish PaymentFlaggedEvent, paymentId={}", event.paymentId());
        }
    }

    public void publishPaymentBlocking(PaymentBlockedEvent event){
        log.info("Publishing PaymentBlocked event to stream");
        boolean sent = streamBridge.send("paymentBlocked-out-0", event);
        if(sent){
            log.info("PaymentBlocked event sent successfully, paymentId={}", event.paymentId());
        }else{
            log.warn("Failed to publish PaymentBlockedEvent, paymentId={}", event.paymentId());
        }
    }
}
