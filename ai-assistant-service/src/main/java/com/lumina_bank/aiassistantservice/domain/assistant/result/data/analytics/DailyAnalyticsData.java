package com.lumina_bank.aiassistantservice.domain.assistant.result.data.analytics;

import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantData;

import java.math.BigDecimal;

public record DailyAnalyticsData (
        BigDecimal income,
        BigDecimal expense,
        Long transactionCount
) implements AssistantData {}
