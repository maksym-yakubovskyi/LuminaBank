package com.lumina_bank.aiassistantservice.domain.result.data.analytics;

import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

import java.math.BigDecimal;

public record DailyAnalyticsData (
        BigDecimal income,
        BigDecimal expense,
        Long transactionCount
) implements AssistantData {}
