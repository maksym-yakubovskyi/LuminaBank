package com.lumina_bank.analyticsservice.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.YearMonth;

@Builder
public record AnalyticsMonthlyOverviewResponse(

        YearMonth month,

        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal cashFlow,

        Long transactionCount

) {}
