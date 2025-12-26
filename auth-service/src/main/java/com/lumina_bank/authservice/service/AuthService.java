package com.lumina_bank.authservice.service;

import com.lumina_bank.authservice.dto.CreatedRefreshToken;
import com.lumina_bank.authservice.dto.LoginRequest;
import com.lumina_bank.authservice.dto.TokensResponse;
import com.lumina_bank.authservice.security.util.JwtProperties;
import com.lumina_bank.authservice.security.model.UserDetailsImpl;
import com.lumina_bank.authservice.security.service.JwtTokenService;
import com.lumina_bank.authservice.security.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;

    public TokensResponse login(LoginRequest req) {
        log.debug("Authentication attempt for email:{}", req.email());

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.email(),
                        req.password()
                )
        );

        var user = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = user.userEntity().getId();

        log.debug("Authentication successful for userId={}", userId);

        CreatedRefreshToken created = refreshTokenService.createNewSession(user.userEntity().getId());

        log.debug("Refresh token created for userId={}, sessionId={}", userId, created.sessionId());

        Set<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        String accessToken = jwtTokenService.generateAccessToken(
                userId,
                roles,
                created.sessionId()
        );

        log.debug("Access token created for userId={}, sessionId={}", userId, created.sessionId());

        return TokensResponse.builder()
                .accessToken(accessToken)
                .refreshToken(created.refreshToken())
                .tokenType(jwtProperties.tokenType())
                .expiresIn(jwtProperties.accessTokenTtl().toSeconds())
                .build();
    }

    public void logout(Long userId, String sessionId) {
        log.debug("Logout attempt for userId={}, sessionId={}", userId, sessionId);
        refreshTokenService.revokeSession(userId, sessionId);
    }

    public void logoutAll(Long userId) {
        log.debug("Logout all sessions for userId={}", userId);
        refreshTokenService.revokeAll(userId);
    }
}