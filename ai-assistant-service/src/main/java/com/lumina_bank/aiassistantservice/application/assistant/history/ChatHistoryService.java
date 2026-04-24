package com.lumina_bank.aiassistantservice.application.assistant.history;

import com.lumina_bank.aiassistantservice.domain.enums.ConversationStatus;
import com.lumina_bank.aiassistantservice.domain.enums.FlowState;
import com.lumina_bank.aiassistantservice.domain.enums.MessageSender;
import com.lumina_bank.aiassistantservice.domain.enums.MessageType;
import com.lumina_bank.aiassistantservice.domain.exception.ConversationNotFoundException;
import com.lumina_bank.aiassistantservice.domain.model.ChatMessage;
import com.lumina_bank.aiassistantservice.domain.model.Conversation;
import com.lumina_bank.aiassistantservice.domain.repository.ChatMessageRepository;
import com.lumina_bank.aiassistantservice.domain.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatHistoryService {
    private final ConversationRepository conversationRepo;
    private final ChatMessageRepository messageRepo;
    private final ChatMemoryCleanupService chatMemoryCleanupService;

    private static final int MAX_MESSAGES = 100;

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
                        new ConversationNotFoundException("Conversation not found"));
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getConversationMessages(UUID conversationId, Long userId) {
        Conversation c = conversationRepo
                .findByIdAndUserId(conversationId,userId)
                .orElseThrow(() -> new ConversationNotFoundException("Conversation not found"));
        return messageRepo.findByConversationOrderByCreatedAt(c);
    }

    @Transactional(readOnly = true)
    public List<Conversation> getUserConversations(Long userId) {
        return conversationRepo.findByUserIdOrderByLastMessageAtDesc(userId);
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
        c.setMessageCount(c.getMessageCount() + 1);

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

    @Transactional
    public void checkAndCloseIfTooLarge(Conversation conversation) {

        if (conversation.getMessageCount() >= MAX_MESSAGES) {
            conversation.setStatus(ConversationStatus.CLOSED);
            saveConversation(conversation);
            chatMemoryCleanupService.deleteByConversationId(conversation.getId());
            log.info("Conversation {} closed due to size limit", conversation.getId());
        }
    }

    @Transactional
    public void deleteConversation(UUID id, Long userId) {

        Conversation conversation = conversationRepo
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ConversationNotFoundException("Conversation not found"));

        chatMemoryCleanupService.deleteByConversationId(conversation.getId());

        messageRepo.deleteByConversation(conversation);

        conversationRepo.delete(conversation);
        log.info("Conversation {} fully deleted", id);
    }
}
