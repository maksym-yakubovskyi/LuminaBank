package com.lumina_bank.notificationservice.infrastructure.messaging.consumer;

import com.lumina_bank.common.dto.event.payment_events.PaymentBlockedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentCompletedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentFlaggedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentReminderEvent;
import com.lumina_bank.common.dto.event.user_events.*;
import com.lumina_bank.notificationservice.application.email.service.EmailNotificationService;
import com.lumina_bank.notificationservice.application.service.UserContactInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class EmailConsumersConfig {
    private final EmailNotificationService emailNotificationService;
    private final UserContactInfoService userContactInfoService;

    //код підтвердження
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

    // користувач зареєструвався
    @Bean
    public Consumer<UserRegisteredEvent> userRegisteredConsumer() {
        return event -> {
            if (event == null) {
                log.warn("Received null UserRegisteredEvent");
                return;
            }

            userContactInfoService.saveOrUpdate(event.authUserId(), event.email());

            switch (event) {
                case IndividualUserRegisteredEvent e -> emailNotificationService.sendWelcomeIndividual(e);

                case BusinessUserRegisteredEvent e -> emailNotificationService.sendWelcomeBusiness(e);

                default ->
                        log.warn("Unknown event type: {}", event.getClass().getName());
            }

        };
    }

    // користувач залогінився
    @Bean
    public Consumer<UserLoginEvent> userLoginConsumer() {
        return event -> {
            if (event == null) {
                log.warn("Received null UserLoginEvent");
                return;
            }
            emailNotificationService.sendLoginEmail(
                    event.email(),
                    parseDevice(event.userAgent()),
                    event.loginTime()
            );
        };
    }

    @Bean
    public Consumer<PaymentCompletedEvent> paymentCompletedConsumer() {
        return event -> {
            if (event == null) {
                log.warn("Received null PaymentCompletedEvent");
                return;
            }

            String email = userContactInfoService.getEmail(event.initiatorUserId());

            if (email == null) {
                log.warn("Email not found for userId={}", event.initiatorUserId());
                return;
            }

            emailNotificationService.sendPaymentCompleted(email, event);
        };
    }

    @Bean
    public Consumer<PaymentFlaggedEvent> paymentFlaggedConsumer() {
        return event -> {
            if (event == null) {
                log.warn("Received null PaymentFlaggedEvent");
                return;
            }

            String email = userContactInfoService.getEmail(event.initiatorUserId());

            if (email == null) {
                log.warn("Email not found for userId={}", event.initiatorUserId());
                return;
            }

            emailNotificationService.sendPaymentFlagged(email, event);
        };
    }

    @Bean
    public Consumer<PaymentBlockedEvent> paymentBlockedConsumer() {
        return event -> {
            if (event == null) {
                log.warn("Received null PaymentBlockedEvent");
                return;
            }

            String email = userContactInfoService.getEmail(event.initiatorUserId());

            if (email == null) {
                log.warn("Email not found for userId={}", event.initiatorUserId());
                return;
            }

            emailNotificationService.sendPaymentBlocked(email, event);
        };
    }

    @Bean
    public Consumer<PaymentReminderEvent> paymentReminderConsumer() {
        return event -> {
            if (event == null) {
                log.warn("Received null PaymentReminderEvent");
                return;
            }

            String email = userContactInfoService.getEmail(event.userId());

            if (email == null) {
                log.warn("Email not found for userId={}", event.userId());
                return;
            }

            emailNotificationService.sendPaymentReminder(email, event);
        };
    }


    private String parseDevice(String userAgent) {
        if (userAgent == null) return "Невідомий пристрій";

        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Mac")) return "macOS";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iPhone")) return "iPhone";

        return "Невідомий пристрій";
    }
}
