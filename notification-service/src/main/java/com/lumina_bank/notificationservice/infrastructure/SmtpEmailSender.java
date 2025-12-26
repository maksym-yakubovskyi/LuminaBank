package com.lumina_bank.notificationservice.infrastructure;

import com.lumina_bank.notificationservice.dto.EmailMessage;
import com.lumina_bank.notificationservice.port.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmtpEmailSender implements EmailSender {
    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

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
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(fromAddress);
        mail.setTo(message.to());
        mail.setSubject(message.subject());
        mail.setText(message.body());

        try{
            mailSender.send(mail);
        }catch(MailException e){
            log.warn("Failed to send email type={}, to={}",message.emailType(),message.to(),e);
            throw e;
        }

        log.debug("SMTP email sent successfully, type={}, to={}", message.emailType(), message.to());
    }

    @Recover
    public void recover(MailException e, EmailMessage message){
        log.warn("All retries failed for email type={}, to={}",message.emailType(),message.to(),e);
    }
}
