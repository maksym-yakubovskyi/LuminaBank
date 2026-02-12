package com.lumina_bank.aiassistantservice.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class OpenAiModelService implements AiModelService {

    private final ChatClient chatClient;

    public OpenAiModelService(ChatClient.Builder builder,ChatMemory chatMemory) {
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

