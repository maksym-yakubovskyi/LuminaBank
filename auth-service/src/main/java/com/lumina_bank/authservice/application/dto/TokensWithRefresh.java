package com.lumina_bank.authservice.application.dto;

import lombok.Builder;

@Builder
public record TokensWithRefresh(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
}
