package com.lumina_bank.aiassistantservice.application.mapper;

import com.lumina_bank.aiassistantservice.api.response.ConversationResponse;
import com.lumina_bank.aiassistantservice.domain.model.Conversation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConversationMapper {

    public ConversationResponse toResponse(Conversation conversation) {
        if (conversation == null) return null;

        return ConversationResponse.builder()
                .id(conversation.getId())
                .status(conversation.getStatus())
                .createdAt(conversation.getCreatedAt())
                .lastMessageAt(conversation.getLastMessageAt())
                .build();
    }

    public List<ConversationResponse> toResponseList(List<Conversation> conversations) {
        return conversations.stream()
                .map(this::toResponse)
                .toList();
    }
}
