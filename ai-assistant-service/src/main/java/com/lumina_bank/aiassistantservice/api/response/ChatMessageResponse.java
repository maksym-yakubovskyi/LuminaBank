package com.lumina_bank.aiassistantservice.api.response;

import com.lumina_bank.aiassistantservice.domain.enums.MessageSender;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ChatMessageResponse(
        UUID id,
        MessageSender sender,
        String content,
        LocalDateTime createdAt
) {
}
