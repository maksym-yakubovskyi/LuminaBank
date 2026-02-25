package com.lumina_bank.aiassistantservice.domain.assistant.result.data.analytics;

import com.lumina_bank.aiassistantservice.infrastructure.external.analytics.dto.AnalyticsTopRecipientResponse;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantData;

import java.util.List;

public record TopRecipientsAnalyticsData(
        List<AnalyticsTopRecipientResponse> recipients
) implements AssistantData {}
