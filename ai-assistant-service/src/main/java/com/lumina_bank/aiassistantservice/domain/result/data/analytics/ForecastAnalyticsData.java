package com.lumina_bank.aiassistantservice.domain.result.data.analytics;

import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

public record ForecastAnalyticsData(
        String forecast,
        String forecastData
) implements AssistantData {}
