package com.lumina_bank.analyticsservice.application.analytics;

import com.lumina_bank.analyticsservice.api.response.analytics.AnalyticsCategoryResponse;
import com.lumina_bank.analyticsservice.api.response.analytics.AnalyticsTopRecipientResponse;
import com.lumina_bank.analyticsservice.api.response.analytics.RecommendationResponse;
import com.lumina_bank.analyticsservice.domain.analysis.FinancialAggregate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    private final FinancialAggregationService aggregationService;
    private final CategoryAnalyticsService categoryService;
    private final AnalyticsQueryService queryService;

    @Transactional(readOnly = true)
    public RecommendationResponse buildRecommendationInfo (Long userId){
        YearMonth currentMonth = YearMonth.now();
        YearMonth fromMonth = currentMonth.minusMonths(5);

        FinancialAggregate agg = aggregationService
                .aggregateLastMonths(userId, 5);

        List<AnalyticsCategoryResponse> topCategories = categoryService
                .getTopCategoriesForPeriod(userId, fromMonth,currentMonth).stream().limit(5).toList();

        List<AnalyticsTopRecipientResponse> topRecipients = queryService
                        .getTopRecipients(userId).stream().limit(5).toList();

        return RecommendationResponse.builder()
                .avgMonthlyExpense(agg.getAvgExpense())
                .avgMonthlyIncome(agg.getAvgIncome())
                .avgMonthlyCashFlow(agg.getAvgCashFlow())
                .expenseGrowthPercent(agg.getExpenseGrowth())
                .incomeGrowthPercent(agg.getIncomeGrowth())
                .avgTransactionAmount(agg.getAvgTransactionAmount())
                .monthlyTransactionCount(agg.getMonthlyTransactionCount())
                .topCategories(topCategories)
                .topRecipients(topRecipients)
                .build();
    }

}
