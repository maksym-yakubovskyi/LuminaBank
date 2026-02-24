package com.lumina_bank.aiassistantservice.domain.assistant.result.data.analytics;

import com.lumina_bank.aiassistantservice.infrastructure.external.analytics.dto.AnalyticsCategoryResponse;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantData;

import java.util.List;

public record CategoryAnalyticsData(
        List<AnalyticsCategoryResponse> categories
)implements AssistantData {
}
