package com.lumina_bank.aiassistantservice.ai;

import com.lumina_bank.common.enums.user.Role;
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

    String generateWithRag(
            String systemPrompt,
            String userPrompt,
            Role role,
            String conversationId
    );
}
