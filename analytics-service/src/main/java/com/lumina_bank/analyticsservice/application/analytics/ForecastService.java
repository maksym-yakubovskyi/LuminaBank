package com.lumina_bank.analyticsservice.application.analytics;

import com.lumina_bank.analyticsservice.api.response.analytics.AiForecastResponse;
import com.lumina_bank.analyticsservice.domain.model.AnalyticsMonthlySummary;
import com.lumina_bank.analyticsservice.domain.repository.AnalyticsMonthlySummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForecastService {
    private final AnalyticsMonthlySummaryRepository monthlyRepo;

    @Transactional(readOnly = true)
    public AiForecastResponse buildForecast(Long userId) {

        YearMonth currentMonth = YearMonth.now();
        YearMonth fromMonth = currentMonth.minusMonths(5);

        List<AnalyticsMonthlySummary> summaries =
                monthlyRepo.findByIdUserIdAndIdYearMonthBetween(
                        userId,
                        fromMonth,
                        currentMonth
                );

        if (summaries.size() < 2) {
            return emptyForecast();
        }

        summaries.sort(
                Comparator.comparing(m -> m.getId().getYearMonth())
        );

        int months = summaries.size();

        List<BigDecimal> expenses = summaries.stream()
                .map(m -> nullSafe(m.getTotalExpense()))
                .toList();

        List<BigDecimal> incomes = summaries.stream()
                .map(m -> nullSafe(m.getTotalIncome()))
                .toList();

        BigDecimal predictedExpense = linearForecast(expenses);
        BigDecimal predictedIncome = linearForecast(incomes);

        BigDecimal predictedCashFlow =
                predictedIncome.subtract(predictedExpense);

        BigDecimal expenseTrendPercent =
                growthPercent(expenses.getFirst(),
                        expenses.getLast());

        BigDecimal incomeTrendPercent =
                growthPercent(incomes.getFirst(),
                        incomes.getLast());

        return new AiForecastResponse(
                predictedIncome,
                predictedExpense,
                predictedCashFlow,
                expenseTrendPercent,
                incomeTrendPercent,
                months
        );
    }

    private BigDecimal linearForecast(List<BigDecimal> values) {

        int n = values.size();

        if (n == 0) {
            return BigDecimal.ZERO;
        }

        if (n == 1) {
            return values.getFirst().setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal sumX = BigDecimal.ZERO;
        BigDecimal sumY = BigDecimal.ZERO;
        BigDecimal sumXY = BigDecimal.ZERO;
        BigDecimal sumXX = BigDecimal.ZERO;

        for (int i = 0; i < n; i++) {

            BigDecimal x = BigDecimal.valueOf(i + 1);
            BigDecimal y = values.get(i);

            sumX = sumX.add(x);
            sumY = sumY.add(y);
            sumXY = sumXY.add(x.multiply(y));
            sumXX = sumXX.add(x.multiply(x));
        }

        BigDecimal bigN = BigDecimal.valueOf(n);

        BigDecimal numerator =
                bigN.multiply(sumXY)
                        .subtract(sumX.multiply(sumY));

        BigDecimal denominator =
                bigN.multiply(sumXX)
                        .subtract(sumX.multiply(sumX));

        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            return values.get(n - 1).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal slope = numerator.divide(
                denominator,
                10,
                RoundingMode.HALF_UP
        );

        BigDecimal intercept = sumY
                .subtract(slope.multiply(sumX))
                .divide(bigN, 10, RoundingMode.HALF_UP);

        BigDecimal nextX = BigDecimal.valueOf(n + 1);

        BigDecimal forecast = intercept.add(
                slope.multiply(nextX)
        );

        return forecast.setScale(2, RoundingMode.HALF_UP);
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

    private BigDecimal nullSafe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private AiForecastResponse emptyForecast() {
        return new AiForecastResponse(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                0
        );
    }
}
