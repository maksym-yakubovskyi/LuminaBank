package com.lumina_bank.aiassistantservice.domain.repository;

import com.lumina_bank.aiassistantservice.domain.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    Optional<Conversation> findByIdAndUserId(UUID conversationId, Long userId);
    List<Conversation> findByUserIdOrderByLastMessageAtDesc(Long userId);
}
