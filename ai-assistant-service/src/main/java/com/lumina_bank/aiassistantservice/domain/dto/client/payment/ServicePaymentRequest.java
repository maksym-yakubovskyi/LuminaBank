package com.lumina_bank.aiassistantservice.domain.dto.client.payment;

import java.math.BigDecimal;

public record ServicePaymentRequest(
    String fromCardNumber,
    Long providerId,
    String category,
    BigDecimal amount,
    String payerReference,
    String description
) {}
