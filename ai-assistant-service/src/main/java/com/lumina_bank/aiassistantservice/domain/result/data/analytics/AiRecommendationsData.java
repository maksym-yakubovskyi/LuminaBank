package com.lumina_bank.aiassistantservice.domain.result.data.analytics;

import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

public record AiRecommendationsData(
        String recommendation,
        String recommendationData
) implements AssistantData {}
