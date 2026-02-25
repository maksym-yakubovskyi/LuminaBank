package com.lumina_bank.aiassistantservice.infrastructure.external.account.dto;

import com.lumina_bank.aiassistantservice.infrastructure.external.account.enums.AccountType;
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
        AccountType type,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
