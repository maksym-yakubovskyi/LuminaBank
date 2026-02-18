package com.lumina_bank.aiassistantservice.domain.dto.client.user;

import com.lumina_bank.common.enums.user.BusinessCategory;

public record BusinessUserUpdateDto(
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
