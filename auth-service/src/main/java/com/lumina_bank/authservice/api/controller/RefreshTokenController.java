package com.lumina_bank.authservice.api.controller;

import com.lumina_bank.authservice.api.response.TokensResponse;
import com.lumina_bank.authservice.application.dto.TokensWithRefresh;
import com.lumina_bank.authservice.application.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        log.info("POST /refresh - Refresh token request received");

        if (refreshToken == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        TokensWithRefresh tokens = refreshTokenService.refreshToken(refreshToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", tokens.refreshToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        log.info("Tokens successfully refreshed");

        return ResponseEntity.ok(
                TokensResponse.builder()
                        .accessToken(tokens.accessToken())
                        .tokenType(tokens.tokenType())
                        .expiresIn(tokens.expiresIn())
                        .build());
    }
}