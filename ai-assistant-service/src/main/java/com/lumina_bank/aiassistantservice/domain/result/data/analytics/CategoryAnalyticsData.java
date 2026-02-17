package com.lumina_bank.aiassistantservice.domain.result.data.analytics;

import com.lumina_bank.aiassistantservice.domain.dto.client.analytics.AnalyticsCategoryResponse;
import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

import java.util.List;

public record CategoryAnalyticsData(
        List<AnalyticsCategoryResponse> categories
)implements AssistantData {
}
