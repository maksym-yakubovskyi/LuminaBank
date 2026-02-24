package com.lumina_bank.aiassistantservice.domain.assistant.result.data.user;

import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantData;

import java.util.Map;

public record BusinessUserUpdatePreviewData (
        Map<String, Object> changes
) implements AssistantData {}
