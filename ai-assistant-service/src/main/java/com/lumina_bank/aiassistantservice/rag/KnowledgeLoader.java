package com.lumina_bank.aiassistantservice.rag;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KnowledgeLoader {

    private final VectorStore vectorStore;
    private final ResourcePatternResolver resolver;

    private static final String KNOWLEDGE_PATH = "classpath:knowledge/**/*.md";

    @PostConstruct
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

            if (isUpToDate(meta.docId(), meta.version())) {
                log.info("Skipping {} (version {})", meta.docId(), meta.version());
                return;
            }

            deleteOldVersion(meta.docId());

            MarkdownDocumentReader reader =
                    new MarkdownDocumentReader(
                            resource,
                            MarkdownDocumentReaderConfig.builder()
                                    .withHorizontalRuleCreateDocument(false)
                                    .withIncludeCodeBlock(false)
                                    .withIncludeBlockquote(true)
                                    .build()
                    );


            List<Document> documents = reader.read();

            if (documents.isEmpty()) {
                return;
            }

            TokenTextSplitter splitter = new TokenTextSplitter(
                    800,   // chunk size (tokens)
                    300,   // min chars
                    5,     // min length
                    10000,
                    true
            );

            List<Document> chunks = splitter.apply(documents);

            for (Document doc : chunks) {
                doc.getMetadata().put("docId", meta.docId());
                doc.getMetadata().put("type", meta.type());
                doc.getMetadata().put("topic", meta.topic());
                doc.getMetadata().put("language", meta.language());
                doc.getMetadata().put("version", meta.version());
                doc.getMetadata().put("source", filename);
            }

            vectorStore.add(chunks);

            System.out.println(chunks);

            log.info("Loaded {} (version {})", meta.docId(), meta.version());

        } catch (Exception e) {
            log.error("Failed to process file {}", resource.getFilename(), e);
        }
    }

    private FileMetadata parseFilename(String filename) {

        String clean = filename.replace(".md", "");
        String[] parts = clean.split("__");

        if (parts.length != 4) {
            throw new IllegalStateException(
                    "Invalid filename format. Expected: type__topic__lang__vX.md"
            );
        }

        String type = parts[0];
        String topic = parts[1];
        String language = parts[2];
        String version = parts[3];

        String docId = type + "_" + topic;

        return new FileMetadata(docId, type, topic, language, version);
    }

    private boolean isUpToDate(String docId, String version) {

        SearchRequest request = SearchRequest.builder()
                .query(docId)
                .topK(1)
                .similarityThreshold(0.7)
                .filterExpression(
                        "docId == '" + docId + "' AND version == '" + version + "'"
                )
                .build();

        List<Document> existing = vectorStore.similaritySearch(request);

        return !existing.isEmpty();
    }

    private void deleteOldVersion(String docId) {
        try {
            vectorStore.delete("docId == '" + docId + "'");
            log.info("Deleted old versions of {}", docId);
        } catch (Exception ignored) {
        }
    }

    private record FileMetadata(
            String docId,
            String type,
            String topic,
            String language,
            String version
    ) {}

}