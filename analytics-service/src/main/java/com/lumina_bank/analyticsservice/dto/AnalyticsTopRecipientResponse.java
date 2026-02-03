package com.lumina_bank.analyticsservice.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AnalyticsTopRecipientResponse(
        Long recipientId,
        String displayName,
        BigDecimal totalAmount,
        Long transactionCount
) {}
