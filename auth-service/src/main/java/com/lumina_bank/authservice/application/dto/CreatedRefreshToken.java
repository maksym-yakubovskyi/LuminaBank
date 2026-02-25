package com.lumina_bank.authservice.application.dto;

public record CreatedRefreshToken(
        String refreshToken,
        String sessionId
) {
}