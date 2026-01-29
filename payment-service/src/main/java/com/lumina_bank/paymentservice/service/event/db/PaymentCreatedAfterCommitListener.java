package com.lumina_bank.paymentservice.service.event.db;

import com.lumina_bank.common.dto.event.payment_events.PaymentCreatedEvent;
import com.lumina_bank.paymentservice.service.event.PaymentEventsPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentCreatedAfterCommitListener {
    private final PaymentEventsPublisher paymentEventsPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(PaymentCreatedEvent event) {
        log.debug("AFTER_COMMIT: publishing PaymentCreatedEvent paymentId={}",event.paymentId());
        paymentEventsPublisher.publishPaymentCreated(event);
    }
}
