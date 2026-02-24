package com.lumina_bank.userservice.api.response;

import com.lumina_bank.userservice.domain.enums.BusinessCategory;
import lombok.Builder;

@Builder
public record BusinessUserProviderResponse(
        Long id,
        String companyName,
        BusinessCategory category
) {}