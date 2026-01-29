package com.lumina_bank.frauddetectionservice.sevice.event;

import com.lumina_bank.common.dto.event.payment_events.PaymentRiskResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventsPublisher {
    private final StreamBridge streamBridge;

    public void paymentRiskResult(PaymentRiskResultEvent event){
        log.info("Publishing PaymentRiskResult event to stream");

        boolean sent = streamBridge.send("paymentRiskResult-out-0", event);

        if(sent){
            log.info("PaymentRiskResult event sent successfully, paymentId={}", event.paymentId());
        }else {
            log.warn("Failed to publish PaymentRiskResultEvent, paymentId={}", event.paymentId());
        }
    }
}
