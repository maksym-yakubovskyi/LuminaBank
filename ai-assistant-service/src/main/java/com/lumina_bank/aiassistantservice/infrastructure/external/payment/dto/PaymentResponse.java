package com.lumina_bank.aiassistantservice.infrastructure.external.payment.dto;

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
