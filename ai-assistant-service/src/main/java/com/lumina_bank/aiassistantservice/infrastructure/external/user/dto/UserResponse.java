package com.lumina_bank.aiassistantservice.infrastructure.external.user.dto;

import java.time.LocalDate;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        LocalDate birthDate,
        Address address,
        String role
) {
}
