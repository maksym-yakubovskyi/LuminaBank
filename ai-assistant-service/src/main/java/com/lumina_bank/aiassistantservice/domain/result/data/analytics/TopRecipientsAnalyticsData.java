package com.lumina_bank.aiassistantservice.domain.result.data.analytics;

import com.lumina_bank.aiassistantservice.domain.dto.client.analytics.AnalyticsTopRecipientResponse;
import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

import java.util.List;

public record TopRecipientsAnalyticsData(
        List<AnalyticsTopRecipientResponse> recipients
) implements AssistantData {}
