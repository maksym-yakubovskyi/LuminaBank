package com.lumina_bank.userservice.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UserUpdateRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank String phoneNumber,
        @NotNull LocalDate birthDate,
        String street,
        String city,
        String houseNumber,
        String zipCode,
        String country
) {
}
