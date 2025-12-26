package com.lumina_bank.authservice.service;

import com.lumina_bank.authservice.repository.EmailVerificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationCleanupService {
    private final EmailVerificationRepository emailVerificationRepository;

    @Transactional
    @Scheduled(cron = "0 0 4 * * *")
    public void cleanup() {
        Instant now = Instant.now();

        log.info("Starting email verification cleanup. Removing emailVerification expired before {}", now);

        int deleteCount = emailVerificationRepository.deleteByExpiresAtBefore(now);

        if (deleteCount > 0) {
            log.info("EmailVerification cleanup has been executed successfully. Deleted {} entities", deleteCount);
        } else {
            log.debug("EmailVerification cleanup completed. No entities to remove");
        }
    }
}