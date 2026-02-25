package com.lumina_bank.aiassistantservice.infrastructure.external.user.dto;

import com.lumina_bank.aiassistantservice.infrastructure.external.user.enums.BusinessCategory;

public record BusinessUserUpdateRequest(
        String email,
        String phoneNumber,
        String companyName,
        String adrpou,
        String description,
        BusinessCategory category,
        String street,
        String city,
        String houseNumber,
        String zipCode,
        String country
) {
}
