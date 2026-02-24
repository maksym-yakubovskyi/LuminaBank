package com.lumina_bank.analyticsservice.application.analytics;

import com.lumina_bank.analyticsservice.domain.analysis.FinancialAggregate;
import com.lumina_bank.analyticsservice.domain.model.AnalyticsMonthlySummary;
import com.lumina_bank.analyticsservice.domain.repository.AnalyticsMonthlySummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialAggregationService {

    private final AnalyticsMonthlySummaryRepository monthlyRepo;

    @Transactional(readOnly = true)
    public FinancialAggregate aggregateLastMonths(Long userId, int monthsBack) {

        YearMonth currentMonth = YearMonth.now();
        YearMonth fromMonth = currentMonth.minusMonths(monthsBack);

        List<AnalyticsMonthlySummary> summaries =
                monthlyRepo.findByIdUserIdAndIdYearMonthBetween(
                        userId,
                        fromMonth,
                        currentMonth
                );

        if (summaries.size() < 2) {
            return FinancialAggregate.empty();
        }

        summaries.sort(Comparator.comparing(m -> m.getId().getYearMonth()));

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        BigDecimal totalCashFlow = BigDecimal.ZERO;
        long totalTransactions = 0;

        for (AnalyticsMonthlySummary m : summaries) {
            totalIncome = totalIncome.add(nullSafe(m.getTotalIncome()));
            totalExpense = totalExpense.add(nullSafe(m.getTotalExpense()));
            totalCashFlow = totalCashFlow.add(nullSafe(m.getCashFlow()));
            totalTransactions += nullSafe(m.getTransactionCount());
        }

        int months = summaries.size();

        return FinancialAggregate.builder()
                .avgIncome(divide(totalIncome, months))
                .avgExpense(divide(totalExpense, months))
                .avgCashFlow(divide(totalCashFlow, months))
                .avgTransactionAmount(
                        totalTransactions == 0
                                ? BigDecimal.ZERO
                                : totalIncome.add(totalExpense)
                                .divide(BigDecimal.valueOf(totalTransactions), 2, RoundingMode.HALF_UP)
                )
                .monthlyTransactionCount((int) (totalTransactions / months))
                .expenseGrowth(growthPercent(
                        nullSafe(summaries.get(months - 2).getTotalExpense()),
                        nullSafe(summaries.get(months - 1).getTotalExpense())
                ))
                .incomeGrowth(growthPercent(
                        nullSafe(summaries.get(months - 2).getTotalIncome()),
                        nullSafe(summaries.get(months - 1).getTotalIncome())
                ))
                .fromMonth(fromMonth)
                .toMonth(currentMonth)
                .build();
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private long nullSafe(Long value) {
        return value == null ? 0L : value;
    }

    private BigDecimal divide(BigDecimal value, int divisor) {
        if (divisor == 0) return BigDecimal.ZERO;
        return value.divide(BigDecimal.valueOf(divisor), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal growthPercent(BigDecimal previous, BigDecimal current) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : BigDecimal.valueOf(100);
        }

        return current.subtract(previous)
                .multiply(BigDecimal.valueOf(100))
                .divide(previous, 2, RoundingMode.HALF_UP);
    }
}
