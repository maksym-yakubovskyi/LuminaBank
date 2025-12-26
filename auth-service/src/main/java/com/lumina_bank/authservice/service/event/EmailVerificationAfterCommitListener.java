package com.lumina_bank.authservice.service.event;

import com.lumina_bank.common.dto.event.user_events.EmailVerificationRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationAfterCommitListener {
    private final EmailEventsPublisher emailEventsPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(EmailVerificationRequestedEvent event) {
        log.debug("AFTER_COMMIT: publishing EmailVerificationRequestedEvent for email={}", event.email());
        emailEventsPublisher.publishVerificationRequested(event);
    }
}
