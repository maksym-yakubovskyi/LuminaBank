package com.lumina_bank.authservice.repository;

import com.lumina_bank.authservice.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByTokenHash(String token);

    @Modifying
    @Query("""
UPDATE RefreshToken rt
SET rt.revoked = true
WHERE rt.user.id = :userId AND rt.sessionId = :sid
""")
    int revokeByUserAndSession(@Param("userId") Long userId,@Param("sid") String sid);

    @Modifying
    @Query("""
UPDATE RefreshToken rt
SET rt.revoked = true
WHERE rt.user.id = :userId
""")
    int revokeAllByUser(@Param("userId") Long userId);

    @Modifying
    @Query("""
    DELETE FROM RefreshToken t
    WHERE t.revoked = true
        AND t.expiresAt < :threshold
    """)
    int deleteExpiredRevoked(@Param("threshold") Instant threshold);
}
