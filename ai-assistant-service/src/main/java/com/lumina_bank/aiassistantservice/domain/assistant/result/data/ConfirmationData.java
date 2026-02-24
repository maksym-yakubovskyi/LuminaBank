package com.lumina_bank.aiassistantservice.domain.assistant.result.data;

import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantData;

import java.util.Map;

public record ConfirmationData(
        String type,
        Map<String,Object> metadata
) implements AssistantData {}

