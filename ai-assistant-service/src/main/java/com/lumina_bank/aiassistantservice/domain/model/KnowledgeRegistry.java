package com.lumina_bank.aiassistantservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "knowledge_registry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeRegistry {

    @Id
    private String docId;

    @Column(nullable = false)
    private String version;

//    @Column
//    private String checksum;

    @Column(nullable = false)
    private LocalDateTime loadedAt;
}