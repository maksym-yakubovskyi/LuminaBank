package com.lumina_bank.authservice.security.service;

import com.lumina_bank.authservice.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenCleanupService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanup() {
        Instant threshold = Instant.now().minus(7, ChronoUnit.DAYS);

        log.info("Starting refresh token cleanup. Removing revoked tokens expired before {}", threshold);

        int deleteCount = refreshTokenRepository.deleteExpiredRevoked(threshold);

        if (deleteCount > 0) {
            log.info("Refresh token cleanup has been executed successfully. Removed {} tokens", deleteCount);
        } else {
            log.debug("Refresh token cleanup completed. No tokens to remove");
        }
    }
}
