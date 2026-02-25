package com.lumina_bank.accountservice.infrastructure.external.analytics.dto;

import java.math.BigDecimal;

public record LoanInfoExternalResponse(
        BigDecimal avgMonthlyExpense,
        BigDecimal avgMonthlyIncome,
        BigDecimal avgMonthlyCashFlow,
        BigDecimal expenseGrowthPercent,
        BigDecimal incomeGrowthPercent,
        BigDecimal avgTransactionAmount,
        Integer monthlyTransactionCount
) {}
