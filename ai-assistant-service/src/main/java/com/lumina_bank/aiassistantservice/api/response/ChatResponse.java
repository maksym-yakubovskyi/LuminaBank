package com.lumina_bank.aiassistantservice.api.response;

public record ChatResponse(
        String type,
        String message,
        String conversationId
) {}