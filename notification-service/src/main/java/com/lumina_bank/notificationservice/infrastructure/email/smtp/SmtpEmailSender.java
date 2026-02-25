package com.lumina_bank.notificationservice.infrastructure.email.smtp;

import com.lumina_bank.notificationservice.domain.email.EmailMessage;
import com.lumina_bank.notificationservice.application.email.port.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmtpEmailSender implements EmailSender {
    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.mail.from-name}")
    private String fromName;

    @Retryable(
            retryFor = MailException.class,
            backoff = @Backoff(
                    delay = 2000,
                    multiplier = 2,
                    maxDelay = 5000
            )
    )
    @Override
    public void send(EmailMessage message) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try{
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    false,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(new InternetAddress(fromAddress,fromName));
            helper.setTo(message.to());
            helper.setSubject(message.subject());
            helper.setText(message.body(),true);

            mailSender.send(mimeMessage);

        } catch (MessagingException | UnsupportedEncodingException e) {

            log.warn("Failed to send email type={}, to={}", message.emailType(), message.to(), e);

            throw new MailSendException("Failed to send email", e);
        }

        log.debug("SMTP email sent successfully, type={}, to={}", message.emailType(), message.to());
    }

    @Recover
    public void recover(MailException e, EmailMessage message){
        log.warn("All retries failed for email type={}, to={}",message.emailType(),message.to(),e);
    }
}
