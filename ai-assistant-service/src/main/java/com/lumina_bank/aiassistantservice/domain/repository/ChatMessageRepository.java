package com.lumina_bank.aiassistantservice.domain.repository;

import com.lumina_bank.aiassistantservice.domain.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
}
