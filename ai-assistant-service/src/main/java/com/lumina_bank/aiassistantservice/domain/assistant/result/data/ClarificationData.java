package com.lumina_bank.aiassistantservice.domain.assistant.result.data;

import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantData;

public record ClarificationData(
        String message
) implements AssistantData {
}
