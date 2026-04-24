package com.lumina_bank.aiassistantservice.application.assistant.history;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMemoryCleanupService {
    private final JdbcTemplate jdbcTemplate;

    private static final String DELETE_BY_CONVERSATION_SQL =
            "DELETE FROM SPRING_AI_CHAT_MEMORY WHERE conversation_id = ?";

    @Transactional
    public void deleteByConversationId(UUID conversationId) {

        int deleted = jdbcTemplate.update(
                DELETE_BY_CONVERSATION_SQL,
                conversationId.toString()
        );

        log.info("Deleted {} Spring AI memory messages for conversation {}",
                deleted,
                conversationId);
    }
}
