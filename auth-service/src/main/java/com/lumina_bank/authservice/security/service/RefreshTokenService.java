package com.lumina_bank.authservice.security.service;

import com.lumina_bank.authservice.dto.CreatedRefreshToken;
import com.lumina_bank.authservice.dto.TokensWithRefresh;
import com.lumina_bank.authservice.exception.RefreshTokenExpiredException;
import com.lumina_bank.authservice.exception.RevokedTokenException;
import com.lumina_bank.authservice.exception.TokenNotFoundException;
import com.lumina_bank.authservice.model.RefreshToken;
import com.lumina_bank.authservice.model.User;
import com.lumina_bank.authservice.repository.RefreshTokenRepository;
import com.lumina_bank.authservice.security.util.JwtProperties;
import com.lumina_bank.authservice.security.util.RefreshTokenGenerator;
import com.lumina_bank.authservice.security.util.TokenHasher;
import com.lumina_bank.authservice.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private final JwtTokenService jwtTokenService;
    private final UserService userService;
    private final TokenHasher tokenHasher;

    @Transactional
    public CreatedRefreshToken createNewSession(Long userId) {
        String sessionId = generateSessionId();

        log.debug("Creating new session for userId={}, sessionId={}", userId, sessionId);

        return createRefreshToken(userId, sessionId);
    }

    @Transactional
    public CreatedRefreshToken createRefreshToken(Long userId, String sessionId) {
        log.debug("Attempt to create refresh token for userId={}, sessionId={}", userId, sessionId);

        String rawToken = RefreshTokenGenerator.generate();
        String hash = tokenHasher.hash(rawToken);

        Instant now = Instant.now();
        Instant expiresAt = now.plus(jwtProperties.refreshTokenTtl());

        User user = userService.getUserById(userId);

        RefreshToken entity = RefreshToken.builder()
                .tokenHash(hash)
                .user(user)
                .expiresAt(expiresAt)
                .sessionId(sessionId)
                .revoked(Boolean.FALSE)
                .build();

        refreshTokenRepository.save(entity);

        log.debug("Refresh token created for userId={}, sessionId={}, expiresAt={}", userId, sessionId, expiresAt);

        return new CreatedRefreshToken(rawToken, sessionId);
    }

    @Transactional
    public RefreshToken validateAndConsumeRefreshToken(String rawToken) {
        String hash = tokenHasher.hash(rawToken);

        RefreshToken token = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> {
                    log.debug("Token not found");
                    return new TokenNotFoundException("Invalid refresh token");
                });

        if (token.getRevoked()) {
            log.debug("Revoked refresh token used, userId={}, sessionId={}", token.getUser().getId(), token.getSessionId());
            throw new RevokedTokenException("Token has been revoked");
        }

        if (token.getExpiresAt().isBefore(Instant.now())) {
            log.debug("Expired refresh token used, userId={}, sessionId={}", token.getUser().getId(), token.getSessionId());
            throw new RefreshTokenExpiredException("Refresh token expired");
        }

        token.setRevoked(Boolean.TRUE);
        refreshTokenRepository.save(token);

        log.debug("Refresh token consumed and revoked, userId={}, sessionId={}", token.getUser().getId(), token.getSessionId());

        return token;
    }

    @Transactional
    public TokensWithRefresh refreshToken(String rawToken) {
        log.debug("Refresh token attempt");

        RefreshToken old = validateAndConsumeRefreshToken(rawToken);
        User user = userService.getUserById(old.getUser().getId());

        String newAccessToken = jwtTokenService.generateAccessToken(
                user.getId(),
                Set.of(user.getRole().name()),
                old.getSessionId()
        );

        CreatedRefreshToken rotated = createRefreshToken(old.getUser().getId(), old.getSessionId());

        log.debug("Refresh token rotated successfully, userId={}, sessionId={}", user.getId(), old.getSessionId());

        return TokensWithRefresh.builder()
                .accessToken(newAccessToken)
                .refreshToken(rotated.refreshToken())
                .tokenType(jwtProperties.tokenType())
                .expiresIn(jwtProperties.accessTokenTtl().toSeconds())
                .build();
    }

    @Transactional
    public void revokeSession(Long userId, String sessionId) {
        log.debug("Revoking session for userId={}, sessionId={}", userId, sessionId);

        int count = refreshTokenRepository.revokeByUserAndSession(userId, sessionId);

        log.debug("Revoked {} refresh tokens for userId={}, sessionId={}", count, userId, sessionId);
    }

    @Transactional
    public void revokeAll(Long userId) {
        log.debug("Revoking all sessions for userId={}", userId);

        int count = refreshTokenRepository.revokeAllByUser(userId);

        log.debug("Revoked {} refresh tokens for userId={}", count, userId);
    }

    private String generateSessionId() {
        byte[] bytes = new byte[32];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}