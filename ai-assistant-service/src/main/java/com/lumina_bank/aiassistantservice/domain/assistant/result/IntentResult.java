package com.lumina_bank.aiassistantservice.domain.assistant.result;

import com.lumina_bank.aiassistantservice.domain.enums.Intent;

public record IntentResult(
        Intent intent,
        Double confidence
) {}

