package com.lumina_bank.aiassistantservice.infrastructure.external.analytics.dto;

import java.math.BigDecimal;

public record AnalyticsTopRecipientResponse(
        Long recipientId,
        String displayName,
        BigDecimal totalAmount,
        Long transactionCount
) {}
