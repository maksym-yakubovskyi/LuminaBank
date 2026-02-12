package com.lumina_bank.aiassistantservice.ai;

import org.springframework.core.ParameterizedTypeReference;

public interface AiModelService {
    String generateText(
            String systemPrompt,
            String userPrompt,
            String conversationId
    );
    <T> T generateEntity(
            String systemPrompt,
            String userPrompt,
            String conversationId,
            Class<T> responseType
    );

    <T> T generateEntity(
            String systemPrompt,
            String userPrompt,
            String conversationId,
            ParameterizedTypeReference<T> typeRef

    );
}
