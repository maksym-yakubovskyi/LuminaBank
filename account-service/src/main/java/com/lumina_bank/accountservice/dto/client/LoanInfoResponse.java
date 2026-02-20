package com.lumina_bank.accountservice.dto.client;

import java.math.BigDecimal;

public record LoanInfoResponse(
        BigDecimal avgMonthlyExpense,
        BigDecimal avgMonthlyIncome,
        BigDecimal avgMonthlyCashFlow,
        BigDecimal expenseGrowthPercent,
        BigDecimal incomeGrowthPercent,
        BigDecimal avgTransactionAmount,
        Integer monthlyTransactionCount
) {}
