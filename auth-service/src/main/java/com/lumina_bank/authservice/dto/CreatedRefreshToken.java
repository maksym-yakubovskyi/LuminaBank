package com.lumina_bank.authservice.dto;

public record CreatedRefreshToken(
        String refreshToken,
        String sessionId
) {
}