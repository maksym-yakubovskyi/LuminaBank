package com.lumina_bank.aiassistantservice.domain.result.data.info;

import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

public record FinalResponseData(
        String text
) implements AssistantData {}
