package com.lumina_bank.aiassistantservice.domain.result.data;

import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

import java.util.Map;

public record ConfirmationData(
    String type,
    Map<String,Object> metadata
) implements AssistantData {}
