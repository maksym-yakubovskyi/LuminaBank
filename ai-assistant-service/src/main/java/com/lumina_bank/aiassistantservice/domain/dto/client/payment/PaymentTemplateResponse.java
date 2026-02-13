package com.lumina_bank.aiassistantservice.domain.dto.client.payment;

import java.math.BigDecimal;

public record PaymentTemplateResponse(
        Long id,
        Long userId,
        String type,
        String name,
        String description,
        String fromCardNumber,
        String toCardNumber,
        BigDecimal amount,
        Boolean isRecurring,
        String nextExecutionTime
) {
}
