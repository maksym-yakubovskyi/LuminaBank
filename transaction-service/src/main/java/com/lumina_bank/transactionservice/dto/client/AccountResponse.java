package com.lumina_bank.transactionservice.dto.client;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AccountResponse(
        Long id,
        Long userId,
        BigDecimal balance,
        String iban,
        String currency,
        String status,
        String type,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
