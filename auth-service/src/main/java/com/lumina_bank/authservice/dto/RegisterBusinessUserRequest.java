package com.lumina_bank.authservice.dto;

import com.lumina_bank.common.enums.user.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterBusinessUserRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 32, message = "Password must be between 6 nad 32 characters")
        String password,

        @NotBlank(message = "Verification code is required")
        String verificationCode,

        @NotBlank(message = "Last name is required")
        String companyName,
        @NotBlank(message = "ADRPOU is required")
        String adrpou,

        @NotBlank(message = "Category is required")
        String category,

        @NotBlank(message = "Phone number is required")
        String phoneNumber,

        @NotNull(message = "User type is required")
        UserType userType
) {
}