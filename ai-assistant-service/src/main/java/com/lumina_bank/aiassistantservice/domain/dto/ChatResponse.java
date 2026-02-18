package com.lumina_bank.aiassistantservice.domain.dto;

public record ChatResponse(
        String type,
        String message,
        String conversationId
) {}