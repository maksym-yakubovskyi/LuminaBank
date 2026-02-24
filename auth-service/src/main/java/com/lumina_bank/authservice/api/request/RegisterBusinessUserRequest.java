package com.lumina_bank.authservice.api.request;

import com.lumina_bank.common.enums.user.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterBusinessUserRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 32) String password,
        @NotBlank String verificationCode,
        @NotBlank String companyName,
        @NotBlank String adrpou,
        @NotBlank String category,
        @NotBlank String phoneNumber,
        @NotNull UserType userType
) {}