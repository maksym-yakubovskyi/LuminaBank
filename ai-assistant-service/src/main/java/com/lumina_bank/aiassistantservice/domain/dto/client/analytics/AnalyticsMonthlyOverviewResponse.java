package com.lumina_bank.aiassistantservice.domain.dto.client.analytics;

import java.math.BigDecimal;
import java.time.YearMonth;

public record AnalyticsMonthlyOverviewResponse(

        YearMonth month,

        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal cashFlow,

        Long transactionCount

) {}
