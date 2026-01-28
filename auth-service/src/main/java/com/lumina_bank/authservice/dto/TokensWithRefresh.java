package com.lumina_bank.authservice.dto;

import lombok.Builder;

@Builder
public record TokensWithRefresh(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
}
