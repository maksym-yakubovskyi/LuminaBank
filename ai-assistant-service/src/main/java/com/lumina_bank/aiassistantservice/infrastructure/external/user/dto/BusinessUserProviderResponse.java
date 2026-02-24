package com.lumina_bank.aiassistantservice.infrastructure.external.user.dto;

public record BusinessUserProviderResponse(
        Long id,
        String companyName,
        String category
) {
}
