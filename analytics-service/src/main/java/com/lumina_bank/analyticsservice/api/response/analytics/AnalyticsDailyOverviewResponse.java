package com.lumina_bank.analyticsservice.api.response.analytics;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record AnalyticsDailyOverviewResponse(
        LocalDate date,

        BigDecimal totalIncome,
        BigDecimal totalExpense,

        Long transactionCount
) {
}
