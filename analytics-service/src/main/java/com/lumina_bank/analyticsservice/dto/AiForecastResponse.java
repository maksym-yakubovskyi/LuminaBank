package com.lumina_bank.analyticsservice.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AiForecastResponse(
        BigDecimal predictedIncome,
        BigDecimal predictedExpense,
        BigDecimal predictedCashFlow,
        BigDecimal expenseTrendPercent,
        BigDecimal incomeTrendPercent,
        int monthsAnalyzed
) {}
