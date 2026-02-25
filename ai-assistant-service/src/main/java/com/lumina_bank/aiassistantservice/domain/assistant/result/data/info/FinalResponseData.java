package com.lumina_bank.aiassistantservice.domain.assistant.result.data.info;

import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantData;

public record FinalResponseData(
        String text
) implements AssistantData {}
