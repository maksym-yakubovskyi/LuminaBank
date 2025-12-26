package com.lumina_bank.authservice.service.event;

import com.lumina_bank.common.dto.event.user_events.EmailVerificationRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailEventsPublisher {
    private final StreamBridge streamBridge;

    public void publishVerificationRequested(EmailVerificationRequestedEvent event) {
        log.info("Publishing email verification requested event");

        boolean sent = streamBridge.send("emailVerificationRequested-out-0", event);

        if (sent) {
            log.info("Email verification requested event sent successfully, for email={}", event.email());
        } else {
            log.warn("Failed to publish EmailVerificationRequestedEvent email={}", event.email());
        }
    }
}
