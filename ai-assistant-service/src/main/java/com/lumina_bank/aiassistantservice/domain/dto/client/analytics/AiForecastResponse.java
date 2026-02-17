package com.lumina_bank.aiassistantservice.domain.dto.client.analytics;

import java.math.BigDecimal;

public record AiForecastResponse(
        BigDecimal predictedIncome,
        BigDecimal predictedExpense,
        BigDecimal predictedCashFlow,
        BigDecimal expenseTrendPercent,
        BigDecimal incomeTrendPercent,
        int monthsAnalyzed
) {}
