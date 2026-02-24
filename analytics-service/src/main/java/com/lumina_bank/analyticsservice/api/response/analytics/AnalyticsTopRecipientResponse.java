package com.lumina_bank.analyticsservice.api.response.analytics;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AnalyticsTopRecipientResponse(
        Long recipientId,
        String displayName,
        BigDecimal totalAmount,
        Long transactionCount
) {}
