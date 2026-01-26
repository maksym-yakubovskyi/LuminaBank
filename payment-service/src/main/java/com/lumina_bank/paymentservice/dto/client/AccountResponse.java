package com.lumina_bank.paymentservice.dto.client;

import com.lumina_bank.common.enums.payment.Currency;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
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