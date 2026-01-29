package com.lumina_bank.authservice.service.event.db;

import com.lumina_bank.authservice.service.event.UserEventsPublisher;
import com.lumina_bank.common.dto.event.user_events.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegisteredAfterCommitListener {
    private final UserEventsPublisher userEventsPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserRegisteredEvent event) {
        log.debug("AFTER_COMMIT: publishing UserRegisteredEvent userId={}", event.authUserId());
        userEventsPublisher.publishUserRegistered(event);
    }
}
