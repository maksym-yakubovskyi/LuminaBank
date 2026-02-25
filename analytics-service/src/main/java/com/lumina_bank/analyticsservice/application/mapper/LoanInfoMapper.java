package com.lumina_bank.analyticsservice.application.mapper;

import com.lumina_bank.analyticsservice.api.response.analytics.LoanInfoResponse;
import com.lumina_bank.analyticsservice.domain.analysis.FinancialAggregate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LoanInfoMapper {

    public LoanInfoResponse toResponse(FinancialAggregate agg) {

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

    private LoanInfoResponse empty() {
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
