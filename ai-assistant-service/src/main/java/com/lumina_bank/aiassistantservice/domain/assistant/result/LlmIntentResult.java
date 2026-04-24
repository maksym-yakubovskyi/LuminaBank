package com.lumina_bank.aiassistantservice.domain.assistant.result;

public record LlmIntentResult(
        String intent,
        Double confidence
) {}
