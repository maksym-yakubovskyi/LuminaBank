package com.lumina_bank.userservice.dto;

import com.lumina_bank.userservice.enums.BusinessCategory;

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
