package com.lumina_bank.aiassistantservice.domain.dto.client.user;

import com.lumina_bank.common.enums.user.BusinessCategory;

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
