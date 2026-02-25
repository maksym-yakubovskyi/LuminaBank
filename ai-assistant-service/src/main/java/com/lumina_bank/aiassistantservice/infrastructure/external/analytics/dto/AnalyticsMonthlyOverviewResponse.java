package com.lumina_bank.aiassistantservice.infrastructure.external.analytics.dto;

import java.math.BigDecimal;
import java.time.YearMonth;

public record AnalyticsMonthlyOverviewResponse(

        YearMonth month,

        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal cashFlow,

        Long transactionCount

) {}
