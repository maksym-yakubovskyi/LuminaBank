package com.lumina_bank.authservice.domain.repository;

import com.lumina_bank.authservice.domain.model.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmail(String email);

    int deleteByExpiresAtBefore(Instant now);
}
