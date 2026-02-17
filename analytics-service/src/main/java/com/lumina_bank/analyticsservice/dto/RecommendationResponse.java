package com.lumina_bank.analyticsservice.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record RecommendationResponse(
        BigDecimal avgMonthlyExpense,
        BigDecimal avgMonthlyIncome,
        BigDecimal avgMonthlyCashFlow,

        BigDecimal expenseGrowthPercent,
        BigDecimal incomeGrowthPercent,

        List<AnalyticsCategoryResponse> topCategories,

        BigDecimal avgTransactionAmount,
        Integer monthlyTransactionCount,

        List<AnalyticsTopRecipientResponse> topRecipients
) {}
