package com.lumina_bank.aiassistantservice.infrastructure.external.payment.dto;

import java.math.BigDecimal;

public record ServicePaymentRequest(
        String fromCardNumber,
        Long providerId,
        String category,
        BigDecimal amount,
        String payerReference,
        String description
) {}
