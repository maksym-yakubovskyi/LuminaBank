package com.lumina_bank.aiassistantservice.domain.dto.client.user;

public record BusinessUserProviderResponse(
        Long id,
        String companyName,
        String category
) {
}
