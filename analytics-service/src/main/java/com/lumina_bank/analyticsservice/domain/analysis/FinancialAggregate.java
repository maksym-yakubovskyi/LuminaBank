package com.lumina_bank.analyticsservice.domain.analysis;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@Builder
public class FinancialAggregate {

    private final BigDecimal avgIncome;
    private final BigDecimal avgExpense;
    private final BigDecimal avgCashFlow;

    private final BigDecimal expenseGrowth;
    private final BigDecimal incomeGrowth;

    private final BigDecimal avgTransactionAmount;
    private final int monthlyTransactionCount;

    private final YearMonth fromMonth;
    private final YearMonth toMonth;

    public static FinancialAggregate empty() {
        return FinancialAggregate.builder()
                .avgIncome(BigDecimal.ZERO)
                .avgExpense(BigDecimal.ZERO)
                .avgCashFlow(BigDecimal.ZERO)
                .expenseGrowth(BigDecimal.ZERO)
                .incomeGrowth(BigDecimal.ZERO)
                .avgTransactionAmount(BigDecimal.ZERO)
                .monthlyTransactionCount(0)
                .fromMonth(null)
                .toMonth(null)
                .build();
    }

    public boolean isEmpty() {
        return avgIncome.compareTo(BigDecimal.ZERO) == 0
                && avgExpense.compareTo(BigDecimal.ZERO) == 0
                && avgCashFlow.compareTo(BigDecimal.ZERO) == 0
                && monthlyTransactionCount == 0;
    }
}
