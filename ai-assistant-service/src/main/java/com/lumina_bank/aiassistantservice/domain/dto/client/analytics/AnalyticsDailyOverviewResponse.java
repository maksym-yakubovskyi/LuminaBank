package com.lumina_bank.aiassistantservice.domain.dto.client.analytics;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AnalyticsDailyOverviewResponse(
        LocalDate date,

        BigDecimal totalIncome,
        BigDecimal totalExpense,

        Long transactionCount
) {
}
