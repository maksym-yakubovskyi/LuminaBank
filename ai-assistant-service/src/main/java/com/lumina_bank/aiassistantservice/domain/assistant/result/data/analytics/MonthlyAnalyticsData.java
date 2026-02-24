package com.lumina_bank.aiassistantservice.domain.assistant.result.data.analytics;

import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantData;

import java.math.BigDecimal;

public record MonthlyAnalyticsData(
        BigDecimal income,
        BigDecimal expense,
        BigDecimal cashFlow,
        Long transactionCount
) implements AssistantData {}

