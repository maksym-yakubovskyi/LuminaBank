package com.lumina_bank.notificationservice.service;

import com.lumina_bank.notificationservice.dto.EmailMessage;
import com.lumina_bank.notificationservice.enums.EmailType;
import com.lumina_bank.notificationservice.port.EmailSender;
import com.lumina_bank.notificationservice.template.EmailTemplateRenderer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
                .subject("Email Verification")
                .body(templateRenderer.renderVerification(code))
                .emailType(EmailType.VERIFICATION)
                .build();

        log.debug("Sending verification email to={}", email);

        emailSender.send(message);

        log.info("Verification email sent, type={}, to={}", message.emailType(), message.to());
    }
}