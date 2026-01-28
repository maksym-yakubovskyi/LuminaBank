package com.lumina_bank.userservice.dto;

import com.lumina_bank.userservice.model.BusinessUser;
import lombok.Builder;

@Builder
public record BusinessUserProviderResponse(
        Long id,
        String companyName,
        String category
) {
    public static BusinessUserProviderResponse fromEntity(BusinessUser user) {
        return BusinessUserProviderResponse.builder()
                .id(user.getId())
                .companyName(user.getCompanyName())
                .category(user.getCategory().name())
                .build();
    }
}