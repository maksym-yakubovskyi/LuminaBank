package com.lumina_bank.userservice.api.request;

import com.lumina_bank.userservice.domain.enums.BusinessCategory;

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
