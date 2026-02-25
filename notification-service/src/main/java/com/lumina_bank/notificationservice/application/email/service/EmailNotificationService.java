package com.lumina_bank.notificationservice.application.email.service;

import com.lumina_bank.common.dto.event.payment_events.PaymentBlockedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentCompletedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentFlaggedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentReminderEvent;
import com.lumina_bank.common.dto.event.user_events.BusinessUserRegisteredEvent;
import com.lumina_bank.common.dto.event.user_events.IndividualUserRegisteredEvent;
import com.lumina_bank.notificationservice.domain.email.EmailMessage;
import com.lumina_bank.notificationservice.domain.enums.EmailType;
import com.lumina_bank.notificationservice.application.email.port.EmailSender;
import com.lumina_bank.notificationservice.application.email.template.EmailTemplateRenderer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {
    private final EmailTemplateRenderer templateRenderer;
    private final EmailSender emailSender;

    @Async("emailExecutor")
    public void sendVerificationEmail(String email, String code) {
        log.info("sendVerificationEmail - Thread: {}", Thread.currentThread().getName());

        EmailMessage message = EmailMessage.builder()
                .to(email)
                .subject("Код підтвердження")
                .body(templateRenderer.renderVerification(code))
                .emailType(EmailType.VERIFICATION)
                .build();

        log.debug("Sending verification email to={}", email);

        emailSender.send(message);

        log.info("Verification email sent, type={}, to={}", message.emailType(), message.to());
    }

    @Async("emailExecutor")
    public void sendLoginEmail(String email, String device, LocalDateTime loginTime) {
        EmailMessage message = EmailMessage.builder()
                .to(email)
                .subject("Вхід у ваш акаунт")
                .body(templateRenderer.renderLogin(device,loginTime))
                .emailType(EmailType.LOGIN)
                .build();

        log.debug("Sending login email to={}", email);

        emailSender.send(message);

        log.info("Login email sent, type={}, to={}", message.emailType(), message.to());
    }

    @Async("emailExecutor")
    public void sendWelcomeIndividual(IndividualUserRegisteredEvent event) {

        EmailMessage message = EmailMessage.builder()
                .to(event.email())
                .subject("Ласкаво просимо до сервісу")
                .body(templateRenderer.renderWelcomeIndividual(event))
                .emailType(EmailType.WELCOME_INDIVIDUAL)
                .build();

        emailSender.send(message);

        log.info("Welcome individual email sent, userId={}, email={}",
                event.authUserId(), event.email());
    }

    @Async("emailExecutor")
    public void sendWelcomeBusiness(BusinessUserRegisteredEvent event) {

        EmailMessage message = EmailMessage.builder()
                .to(event.email())
                .subject("Реєстрація компанії завершена")
                .body(templateRenderer.renderWelcomeBusiness(event))
                .emailType(EmailType.WELCOME_BUSINESS)
                .build();

        emailSender.send(message);

        log.info("Welcome business email sent, userId={}, email={}",
                event.authUserId(), event.email());
    }

    @Async("emailExecutor")
    public void sendPaymentCompleted(String email, PaymentCompletedEvent event) {

        EmailMessage message = EmailMessage.builder()
                .to(email)
                .subject("Платіж виконано")
                .body(templateRenderer.renderPaymentCompleted(event))
                .emailType(EmailType.PAYMENT_COMPLETED)
                .build();

        emailSender.send(message);

        log.info("Payment completed email sent, paymentId={}, email={}",
                event.paymentId(), email);
    }

    @Async("emailExecutor")
    public void sendPaymentFlagged(String email, PaymentFlaggedEvent event) {

        EmailMessage message = EmailMessage.builder()
                .to(email)
                .subject("Платіж на перевірці")
                .body(templateRenderer.renderPaymentFlagged(event))
                .emailType(EmailType.PAYMENT_FLAGGED)
                .build();

        emailSender.send(message);

        log.info("Payment flagged email sent, paymentId={}, email={}",
                event.paymentId(), email);
    }

    @Async("emailExecutor")
    public void sendPaymentBlocked(String email, PaymentBlockedEvent event) {

        EmailMessage message = EmailMessage.builder()
                .to(email)
                .subject("Платіж заблоковано")
                .body(templateRenderer.renderPaymentBlocked(event))
                .emailType(EmailType.PAYMENT_BLOCKED)
                .build();

        emailSender.send(message);

        log.info("Payment blocked email sent, paymentId={}, email={}",
                event.paymentId(), email);
    }

    @Async("emailExecutor")
    public void sendPaymentReminder(String email, PaymentReminderEvent event) {

        EmailMessage message = EmailMessage.builder()
                .to(email)
                .subject("Нагадування про запланований платіж")
                .body(templateRenderer.renderPaymentReminder(event))
                .emailType(EmailType.PAYMENT_REMINDER)
                .build();

        emailSender.send(message);
        log.info("Payment reminder email sent to={}, templateName={}",
                email, event.templateName());
    }
}