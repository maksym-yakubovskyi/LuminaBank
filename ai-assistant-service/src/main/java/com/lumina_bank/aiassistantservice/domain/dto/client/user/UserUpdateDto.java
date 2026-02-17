package com.lumina_bank.aiassistantservice.domain.dto.client.user;

import java.time.LocalDate;

public record UserUpdateDto(
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