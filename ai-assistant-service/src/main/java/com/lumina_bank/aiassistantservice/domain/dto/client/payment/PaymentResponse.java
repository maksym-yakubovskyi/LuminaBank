package com.lumina_bank.aiassistantservice.domain.dto.client.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        String fromCardNumber,
        String toCardNumber,
        BigDecimal amount,
        String description,
        String status,
        LocalDateTime createdAt
) {
}
