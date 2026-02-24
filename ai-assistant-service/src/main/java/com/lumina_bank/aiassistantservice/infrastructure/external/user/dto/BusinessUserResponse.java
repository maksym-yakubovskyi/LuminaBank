package com.lumina_bank.aiassistantservice.infrastructure.external.user.dto;

import com.lumina_bank.aiassistantservice.infrastructure.external.user.enums.BusinessCategory;

public record BusinessUserResponse(
        Long id,
        String companyName,
        String email,
        String phoneNumber,
        String adrpou,
        String description,
        BusinessCategory category,
        Address address,
        String role
) {
}
