package com.lumina_bank.authservice.controller;

import com.lumina_bank.authservice.dto.RefreshRequest;
import com.lumina_bank.authservice.dto.TokensResponse;
import com.lumina_bank.authservice.security.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshRequest refreshRequest) {
        log.info("POST /refresh - Refresh token request received");

        TokensResponse tokensResponse = refreshTokenService.refreshToken(refreshRequest);

        log.info("Tokens successfully refreshed");

        return ResponseEntity.ok(tokensResponse);
    }
}