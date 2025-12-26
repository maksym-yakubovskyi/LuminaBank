package com.lumina_bank.authservice.service;

import com.lumina_bank.authservice.exception.EmailVerificationException;
import com.lumina_bank.authservice.exception.EmailVerificationNotFoundException;
import com.lumina_bank.authservice.exception.UserAlreadyExistsException;
import com.lumina_bank.authservice.model.EmailVerification;
import com.lumina_bank.authservice.repository.EmailVerificationRepository;
import com.lumina_bank.authservice.repository.UserRepository;
import com.lumina_bank.authservice.security.util.VerificationCodeGenerator;
import com.lumina_bank.common.dto.event.user_events.EmailVerificationRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {
    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void sendVerificationCode(String email) {
        log.debug("Generating verification code for email={}", email);

        validateUserEmail(email);

        String code = VerificationCodeGenerator.generateVerificationCode();
        Instant expiresAt = Instant.now().plus(5, ChronoUnit.MINUTES);

        emailVerificationRepository.save(
                EmailVerification.builder()
                        .email(email)
                        .code(code)
                        .expiresAt(expiresAt)
                        .build()
        );

        eventPublisher.publishEvent(new EmailVerificationRequestedEvent(email, code));

        log.debug("Verification code generated and saved for email={}", email);
    }

    @Transactional(readOnly = true)
    public void validateVerificationCode(String email, String code) {
        log.debug("Validating verification code for email={}", email);

        validateUserEmail(email);

        EmailVerification verification = emailVerificationRepository.findByEmail(email)
                .orElseThrow(() -> new EmailVerificationNotFoundException("Verification code not found for email=" + email));

        if (!verification.getCode().equals(code)) {
            log.debug("Invalid verification code for email={}", email);
            throw new EmailVerificationException("Invalid verification code");
        }

        if (verification.getExpiresAt().isBefore(Instant.now())) {
            log.debug("Verification code expired for email={}", email);
            throw new EmailVerificationException("Verification code expired");
        }

        log.info("Verification code validated successfully for email={}", email);
    }

    public EmailVerification getVerificationByEmail(String email) {
        return emailVerificationRepository.findByEmail(email)
                .orElseThrow(() -> new EmailVerificationNotFoundException("Email verification not found with email=" + email));
    }

    @Transactional
    public void deleteVerificationCode(String email) {
        log.debug("Deleting verification code for email={}", email);
        EmailVerification verification = getVerificationByEmail(email);
        emailVerificationRepository.delete(verification);
        log.info("Verification code deleted for email={}", email);
    }

    private void validateUserEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            log.debug("User with email={} already exists", email);
            throw new UserAlreadyExistsException("User already exists with email=" + email);
        }
    }
}