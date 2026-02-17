package com.lumina_bank.aiassistantservice.domain.result.data.analytics;

import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

import java.math.BigDecimal;

public record MonthlyAnalyticsData(
        BigDecimal income,
        BigDecimal expense,
        BigDecimal cashFlow,
        Long transactionCount
) implements AssistantData {}

