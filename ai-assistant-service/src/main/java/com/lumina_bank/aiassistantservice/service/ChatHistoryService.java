package com.lumina_bank.aiassistantservice.service;

import com.lumina_bank.aiassistantservice.domain.enums.ConversationStatus;
import com.lumina_bank.aiassistantservice.domain.enums.FlowState;
import com.lumina_bank.aiassistantservice.domain.enums.MessageSender;
import com.lumina_bank.aiassistantservice.domain.enums.MessageType;
import com.lumina_bank.aiassistantservice.domain.model.ChatMessage;
import com.lumina_bank.aiassistantservice.domain.model.Conversation;
import com.lumina_bank.aiassistantservice.repository.ChatMessageRepository;
import com.lumina_bank.aiassistantservice.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatHistoryService {
    private final ConversationRepository conversationRepo;
    private final ChatMessageRepository messageRepo;

    @Transactional(readOnly = true)
    public Conversation getOrCreateConversation(UUID conversationId, Long userId) {
        if (conversationId == null) {
            Conversation c = new Conversation();
            c.setUserId(userId);
            c.setFlowState(FlowState.IDLE);
            c.setStatus(ConversationStatus.ACTIVE);
            c.setLastMessageAt(LocalDateTime.now());
            return conversationRepo.save(c);
        }

        return conversationRepo.findByIdAndUserId(conversationId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Conversation not found"));
    }

    @Transactional
    public void saveConversation(Conversation conversation) {
        conversationRepo.save(conversation);
    }
    @Transactional
    public void saveUserMessage(Conversation c, String text) {
        ChatMessage msg = new ChatMessage();
        msg.setConversation(c);
        msg.setSender(MessageSender.USER);
        msg.setType(MessageType.INFO);
        msg.setContent(text);

        c.setLastMessageAt(LocalDateTime.now());
        conversationRepo.save(c);

        messageRepo.save(msg);
    }

    @Transactional
    public void saveAssistantMessage(Conversation c, MessageType type, String text) {
        ChatMessage msg = new ChatMessage();
        msg.setConversation(c);
        msg.setSender(MessageSender.ASSISTANT);
        msg.setType(type);
        msg.setContent(text);

        c.setLastMessageAt(LocalDateTime.now());
        conversationRepo.save(c);

        messageRepo.save(msg);
    }
}
