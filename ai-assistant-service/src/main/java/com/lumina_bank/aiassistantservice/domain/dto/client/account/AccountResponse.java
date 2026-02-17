package com.lumina_bank.aiassistantservice.domain.dto.client.account;

import com.lumina_bank.common.enums.payment.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        Long id,
        Long userId,
        BigDecimal balance,
        String iban,
        Currency currency,
        String status,
        String type,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
