package com.lumina_bank.aiassistantservice.api.response;

import com.lumina_bank.aiassistantservice.domain.enums.MessageType;

public record ChatResponse(
        MessageType type,
        String message,
        String conversationId
) {}