package com.lumina_bank.analyticsservice.api.response.analytics;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record LoanInfoResponse(
        BigDecimal avgMonthlyExpense,
        BigDecimal avgMonthlyIncome,
        BigDecimal avgMonthlyCashFlow,
        BigDecimal expenseGrowthPercent,
        BigDecimal incomeGrowthPercent,
        BigDecimal avgTransactionAmount,
        Integer monthlyTransactionCount
) {}