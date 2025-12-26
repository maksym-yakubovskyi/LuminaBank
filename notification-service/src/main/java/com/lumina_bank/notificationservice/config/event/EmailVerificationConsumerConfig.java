package com.lumina_bank.notificationservice.config.event;

import com.lumina_bank.common.dto.event.user_events.EmailVerificationRequestedEvent;
import com.lumina_bank.notificationservice.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationConsumerConfig {
    private final EmailNotificationService emailNotificationService;

    @Bean
    public Consumer<EmailVerificationRequestedEvent> emailVerificationRequestedConsumer() {
        return event -> {
            if (event == null) {
                log.warn("Received null EmailVerificationRequestedEvent");
                return;
            }
            log.info("Received EmailVerificationRequestedEvent email={}", event.email());

            emailNotificationService.sendVerificationEmail(
                    event.email(),
                    event.code()
            );
        };
    }
}
