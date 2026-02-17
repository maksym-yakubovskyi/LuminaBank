package com.lumina_bank.aiassistantservice.domain.result.data.user;

import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

import java.util.Map;

public record UserUpdatePreviewData(
        Map<String, Object> changes
) implements AssistantData {}