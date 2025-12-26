package com.lumina_bank.authservice.dto;

import lombok.Builder;

@Builder
public record TokensResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
}