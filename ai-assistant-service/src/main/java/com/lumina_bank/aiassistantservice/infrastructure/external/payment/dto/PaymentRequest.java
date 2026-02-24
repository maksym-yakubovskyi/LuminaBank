package com.lumina_bank.aiassistantservice.infrastructure.external.payment.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        String fromCardNumber,
        String toCardNumber,
        BigDecimal amount,
        String description
) {
}
