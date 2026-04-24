package com.lumina_bank.aiassistantservice.application.mapper;

import com.lumina_bank.aiassistantservice.api.response.ChatMessageResponse;
import com.lumina_bank.aiassistantservice.domain.model.ChatMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatMessageMapper {

    public ChatMessageResponse toResponse(ChatMessage chatMessage) {
        if (chatMessage == null) return null;

        return ChatMessageResponse.builder()
                .id(chatMessage.getId())
                .sender(chatMessage.getSender())
                .content(chatMessage.getContent())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }

    public List<ChatMessageResponse> toResponseList(List<ChatMessage> chatMessages) {
        return chatMessages.stream()
                .map(this::toResponse)
                .toList();
    }
}
