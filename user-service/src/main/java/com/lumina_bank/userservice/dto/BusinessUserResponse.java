package com.lumina_bank.userservice.dto;

import com.lumina_bank.userservice.enums.BusinessCategory;
import com.lumina_bank.userservice.model.Address;
import com.lumina_bank.userservice.model.BusinessUser;
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
) {
    public static BusinessUserResponse fromEntity(BusinessUser businessUser) {
        return BusinessUserResponse.builder()
                .id(businessUser.getId())
                .companyName(businessUser.getCompanyName())
                .email(businessUser.getEmail())
                .phoneNumber(businessUser.getPhoneNumber())
                .adrpou(businessUser.getAdrpou())
                .description(businessUser.getDescription())
                .category(businessUser.getCategory())
                .address(businessUser.getAddress())
                .role(businessUser.getRole().name())
                .build();
    }
}
