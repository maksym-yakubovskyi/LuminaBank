package com.lumina_bank.aiassistantservice.domain.dto.client.analytics;

import java.math.BigDecimal;

public record AnalyticsTopRecipientResponse(
        Long recipientId,
        String displayName,
        BigDecimal totalAmount,
        Long transactionCount
) {}
