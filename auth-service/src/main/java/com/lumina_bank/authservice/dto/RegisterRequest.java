package com.lumina_bank.authservice.dto;

import com.lumina_bank.common.enums.user.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 32, message = "Password must be between 6 nad 32 characters")
        String password,

        @NotBlank(message = "Verification code is required")
        String verificationCode,

        @NotBlank(message = "First name is required")
        String firstName,
        @NotBlank(message = "Last name is required")
        String lastName,
        @NotBlank(message = "Phone number is required")
        String phoneNumber,
        @NotNull(message = "Birth date is required")
        LocalDate birthDate,

        @NotNull(message = "User type is required")
        UserType userType
) {
}