package com.lumina_bank.aiassistantservice.domain.dto;

import java.util.UUID;

public record ChatRequest(
        String message,
        String conversationId
) {
    public UUID conversationIdAsUuid() {
        return conversationId == null
                ? null
                : UUID.fromString(conversationId);
    }
}
