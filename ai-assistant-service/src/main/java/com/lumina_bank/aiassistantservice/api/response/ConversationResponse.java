package com.lumina_bank.aiassistantservice.api.response;

import com.lumina_bank.aiassistantservice.domain.enums.ConversationStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ConversationResponse(
        UUID id,
        ConversationStatus status,
        LocalDateTime createdAt,
        LocalDateTime lastMessageAt
) {
}
