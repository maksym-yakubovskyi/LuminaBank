package com.lumina_bank.authservice.api.response;

import lombok.Builder;

@Builder
public record TokensResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {}