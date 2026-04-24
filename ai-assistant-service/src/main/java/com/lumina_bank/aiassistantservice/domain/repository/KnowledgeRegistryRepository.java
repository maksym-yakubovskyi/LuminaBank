package com.lumina_bank.aiassistantservice.domain.repository;


import com.lumina_bank.aiassistantservice.domain.model.KnowledgeRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KnowledgeRegistryRepository extends JpaRepository<KnowledgeRegistry, String> {
}
