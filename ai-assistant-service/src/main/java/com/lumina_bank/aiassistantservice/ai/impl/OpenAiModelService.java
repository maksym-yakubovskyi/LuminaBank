package com.lumina_bank.aiassistantservice.ai.impl;

import com.lumina_bank.aiassistantservice.ai.AiModelService;
import com.lumina_bank.common.enums.user.Role;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class OpenAiModelService implements AiModelService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Autowired
    public OpenAiModelService(ChatClient.Builder builder,ChatMemory chatMemory,VectorStore vectorStore) {
        this.chatClient = builder
                .defaultOptions(ChatOptions.builder()
                        .model("gpt-5-nano")
                        .temperature(1.0)
                        .build())
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory)
                                .build()
                )
                .build();

        this.vectorStore = vectorStore;
    }

    @Override
    public <T> T generateEntity(
            String systemPrompt,
            String userPrompt,
            String conversationId,
            Class<T> responseType
    ) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .advisors(a ->
                        a.param(ChatMemory.CONVERSATION_ID, conversationId)
                )
                .call()
                .entity(responseType);
    }

    @Override
    public <T> T generateEntity(
            String systemPrompt,
            String userPrompt,
            String conversationId,
            ParameterizedTypeReference<T> typeRef
    ) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .advisors(a ->
                        a.param(ChatMemory.CONVERSATION_ID, conversationId)
                )
                .call()
                .entity(typeRef);
    }

    @Override
    public String generateWithRag(String systemPrompt, String userPrompt, Role role, String conversationId) {

        if (role == null) {
            return "audience == 'ALL'";
        }

        String audienceFilter = switch (role) {
            case Role.BUSINESS_USER ->
                    "audience == 'BUSINESS' OR audience == 'ALL'";
            case Role.INDIVIDUAL_USER ->
                    "audience == 'INDIVIDUAL' OR audience == 'ALL'";
            default ->
                    "audience == 'ALL'";
        };


        QuestionAnswerAdvisor qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(
                        SearchRequest.builder()
                                .similarityThreshold(0.75)
                                .topK(5)
                                .filterExpression(audienceFilter)
                                .build()
                )
                .build();

        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .advisors(qaAdvisor)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }

    @Override
    public String generateText(
            String systemPrompt,
            String userPrompt,
            String conversationId) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .advisors(a ->
                        a.param(ChatMemory.CONVERSATION_ID, conversationId)
                )
                .call()
                .content();
    }
}

