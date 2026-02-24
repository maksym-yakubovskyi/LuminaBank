package com.lumina_bank.authservice.api.request;

import com.lumina_bank.common.enums.user.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterUserRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 32) String password,
        @NotBlank String verificationCode,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String phoneNumber,
        @NotNull LocalDate birthDate,
        @NotNull UserType userType
) {}