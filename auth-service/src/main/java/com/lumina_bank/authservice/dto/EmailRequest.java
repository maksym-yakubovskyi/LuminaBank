package com.lumina_bank.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email
) {
}