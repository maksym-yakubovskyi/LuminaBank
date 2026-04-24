package com.lumina_bank.aiassistantservice.infrastructure.ai.knowledge;

import com.lumina_bank.aiassistantservice.domain.model.KnowledgeRegistry;
import com.lumina_bank.aiassistantservice.domain.repository.KnowledgeRegistryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KnowledgeLoader {

    private final VectorStore vectorStore;
    private final ResourcePatternResolver resolver;
    private final KnowledgeRegistryRepository registry;

    private static final String KNOWLEDGE_PATH = "classpath:knowledge/**/*.md";

//    @PostConstruct
    public void loadKnowledge() {
        try {
            Resource[] resources = resolver.getResources(KNOWLEDGE_PATH);

            for (Resource resource : resources) {
                process(resource);
            }

            log.info("Knowledge base successfully loaded");

        } catch (Exception e) {
            log.error("Knowledge loading failed", e);
        }
    }

    private void process(Resource resource) {
        try {
            String filename = resource.getFilename();
            if (filename == null || !filename.endsWith(".md")) {
                return;
            }

            FileMetadata meta = parseFilename(filename);

            KnowledgeRegistry existing =
                    registry.findById(meta.docId()).orElse(null);

            if (existing != null && existing.getVersion().equals(meta.version())) {
                log.info("Skipping {} (same version {})",
                        meta.docId(), meta.version());
                return;
            }

            // якщо версія інша — видаляємо з vectorStore
            deleteByDocId(meta.docId());

            List<Document> documents = readMarkdown(resource);

            if (documents.isEmpty()) {
                log.warn("Skipping empty document: {}", filename);
                return;
            }

            List<Document> chunks = splitDocuments(documents);

            enrichMetadata(chunks, meta, filename);

            // додаємо новий
            vectorStore.add(chunks);

            // оновлюємо registry
            registry.save(new KnowledgeRegistry(
                    meta.docId(),
                    meta.version(),
                    LocalDateTime.now()
            ));

            log.info("Loaded {} (version {}) with {} chunks",
                    meta.docId(), meta.version(), chunks.size());

        } catch (Exception e) {
            log.error("Failed to process file {}", resource.getFilename(), e);
        }
    }

    private List<Document> readMarkdown(Resource resource) {
        MarkdownDocumentReader reader =
                new MarkdownDocumentReader(
                        resource,
                        MarkdownDocumentReaderConfig.builder()
                                .withHorizontalRuleCreateDocument(false)
                                .withIncludeCodeBlock(false)
                                .withIncludeBlockquote(true)
                                .build()
                );

        return reader.read();
    }

    private List<Document> splitDocuments(List<Document> documents) {
        TokenTextSplitter splitter = new TokenTextSplitter(
                800,    // chunk size (tokens)
                300,    // min chars
                5,      // min length
                10000,
                true
        );

        return splitter.apply(documents);
    }

    private void enrichMetadata(
            List<Document> chunks,
            FileMetadata meta,
            String source
    ) {
        for (Document doc : chunks) {
            doc.getMetadata().put("docId", meta.docId());
            doc.getMetadata().put("audience", meta.audience());
            doc.getMetadata().put("type", meta.type());
            doc.getMetadata().put("topic", meta.topic());
            doc.getMetadata().put("language", meta.language());
            doc.getMetadata().put("version", meta.version());
            doc.getMetadata().put("source", source);
        }
    }

    private void deleteByDocId(String docId) {
        try {
            vectorStore.delete("docId == '" + docId + "'");
            log.info("Deleted previous versions of {}", docId);
        } catch (Exception e) {
            log.warn("Delete skipped for {} (maybe not exists)", docId);
        }
    }

    private FileMetadata parseFilename(String filename) {

        String clean = filename.replace(".md", "");
        String[] parts = clean.split("__");

        if (parts.length != 5) {
            throw new IllegalStateException(
                    "Invalid filename format. Expected: audience__type__topic__lang__vX.md"
            );
        }
        String audienceRaw = parts[0].toLowerCase();
        String type = parts[1];
        String topic = parts[2];
        String language = parts[3];
        String version = parts[4];

        String audience = resolveAudience(audienceRaw);

        String docId = type + "_" + topic;

        return new FileMetadata(docId,audience, type, topic, language, version);
    }

    private String resolveAudience(String raw) {
        return switch (raw) {
            case "personal" -> "INDIVIDUAL";
            case "business" -> "BUSINESS";
            case "shared" -> "ALL";
            default -> throw new IllegalStateException(
                    "Unknown audience type: " + raw
            );
        };
    }
    private record FileMetadata(
            String docId,
            String audience,
            String type,
            String topic,
            String language,
            String version
    ) {}

}