package com.lumina_bank.aiassistantservice.domain.dto.client.user;

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
