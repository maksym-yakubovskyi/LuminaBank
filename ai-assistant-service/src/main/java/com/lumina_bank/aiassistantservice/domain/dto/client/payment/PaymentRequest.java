package com.lumina_bank.aiassistantservice.domain.dto.client.payment;

import java.math.BigDecimal;

public record PaymentRequest(
        String fromCardNumber,
        String toCardNumber,
        BigDecimal amount,
        String description
) {
}
