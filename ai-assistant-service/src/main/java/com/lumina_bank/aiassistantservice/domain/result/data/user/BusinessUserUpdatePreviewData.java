package com.lumina_bank.aiassistantservice.domain.result.data.user;

import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

import java.util.Map;

public record BusinessUserUpdatePreviewData (
        Map<String, Object> changes
) implements AssistantData {}
