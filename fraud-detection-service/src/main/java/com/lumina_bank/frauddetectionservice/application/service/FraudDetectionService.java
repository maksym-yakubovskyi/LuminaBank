package com.lumina_bank.frauddetectionservice.application.service;

import com.lumina_bank.common.dto.event.payment_events.PaymentCreatedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentRiskResultEvent;
import com.lumina_bank.frauddetectionservice.infrastructure.messaging.producer.PaymentEventsPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionService {
    private final RiskAssessmentService riskAssessmentService;
    private final PaymentEventsPublisher paymentEventsPublisher;

    public void checkPayment(PaymentCreatedEvent event){
        PaymentRiskResultEvent result = riskAssessmentService.assess(event);

        paymentEventsPublisher.paymentRiskResult(result);
    }
}