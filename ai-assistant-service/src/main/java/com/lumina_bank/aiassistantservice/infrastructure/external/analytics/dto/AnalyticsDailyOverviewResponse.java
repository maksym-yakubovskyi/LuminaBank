package com.lumina_bank.aiassistantservice.infrastructure.external.analytics.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AnalyticsDailyOverviewResponse(
        LocalDate date,

        BigDecimal totalIncome,
        BigDecimal totalExpense,

        Long transactionCount
) {
}
