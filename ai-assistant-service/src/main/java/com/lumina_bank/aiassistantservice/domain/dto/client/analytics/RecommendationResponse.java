package com.lumina_bank.aiassistantservice.domain.dto.client.analytics;

import java.math.BigDecimal;
import java.util.List;

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
