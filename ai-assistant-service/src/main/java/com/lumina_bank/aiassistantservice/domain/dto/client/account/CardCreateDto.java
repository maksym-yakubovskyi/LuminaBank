package com.lumina_bank.aiassistantservice.domain.dto.client.account;

import java.math.BigDecimal;

public record CardCreateDto(
        String cardType,
        String cardNetwork,
        BigDecimal limit
) {
}
