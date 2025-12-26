package com.lumina_bank.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RefreshRequest(
        @NotBlank(message = "Refresh token must not be blank")
        @Size(max = 2048, message = "Refresh token is too long")
        String refreshToken
) {
}