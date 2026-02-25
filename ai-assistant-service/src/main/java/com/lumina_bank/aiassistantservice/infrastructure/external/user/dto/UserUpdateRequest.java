package com.lumina_bank.aiassistantservice.infrastructure.external.user.dto;

import java.time.LocalDate;

public record UserUpdateRequest(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        LocalDate birthDate,
        String street,
        String city,
        String houseNumber,
        String zipCode,
        String country
) {
}