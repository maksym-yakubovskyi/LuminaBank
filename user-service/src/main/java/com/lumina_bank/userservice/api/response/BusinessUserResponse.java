package com.lumina_bank.userservice.api.response;

import com.lumina_bank.userservice.domain.enums.BusinessCategory;
import com.lumina_bank.userservice.domain.model.Address;
import lombok.Builder;

@Builder
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
) {}
