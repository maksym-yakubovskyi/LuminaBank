package com.lumina_bank.analyticsservice.dto;

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
) {

    public static LoanInfoResponse from(FinancialAggregate agg) {

        if (agg == null || agg.isEmpty()) {
            return empty();
        }

        return LoanInfoResponse.builder()
                .avgMonthlyExpense(agg.getAvgExpense())
                .avgMonthlyIncome(agg.getAvgIncome())
                .avgMonthlyCashFlow(agg.getAvgCashFlow())
                .expenseGrowthPercent(agg.getExpenseGrowth())
                .incomeGrowthPercent(agg.getIncomeGrowth())
                .avgTransactionAmount(agg.getAvgTransactionAmount())
                .monthlyTransactionCount(agg.getMonthlyTransactionCount())
                .build();
    }

    public static LoanInfoResponse empty() {
        return LoanInfoResponse.builder()
                .avgMonthlyExpense(BigDecimal.ZERO)
                .avgMonthlyIncome(BigDecimal.ZERO)
                .avgMonthlyCashFlow(BigDecimal.ZERO)
                .expenseGrowthPercent(BigDecimal.ZERO)
                .incomeGrowthPercent(BigDecimal.ZERO)
                .avgTransactionAmount(BigDecimal.ZERO)
                .monthlyTransactionCount(0)
                .build();
    }
}

