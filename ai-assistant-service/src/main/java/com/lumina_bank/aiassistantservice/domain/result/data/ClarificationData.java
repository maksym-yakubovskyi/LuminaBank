package com.lumina_bank.aiassistantservice.domain.result.data;

import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

public record ClarificationData(
        String message
) implements AssistantData {
}
