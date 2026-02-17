package com.lumina_bank.aiassistantservice.domain.intent.impl.info;

import com.lumina_bank.aiassistantservice.ai.AiModelService;
import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.data.info.FinalResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class KnowledgeQueryIntent implements IntentDefinition {
    private final AiModelService aiModelService;

    @Override
    public Intent intent() {
        return Intent.KNOWLEDGE_QUERY;
    }

    private static final String SYSTEM_PROMPT = """
        You are Ava, a trusted financial education assistant
        inside a digital banking application.
    
        Identity:
        - Your name is Ava.
        - You are calm, intelligent, supportive and trustworthy.
        - You explain financial topics in an understandable way.
        
        Language rules:
        - Detect the language of the user's message.
        - Always respond in the SAME language as the user.
        - If the language is unclear → default to Ukrainian.
    
        Core rules:
        - Answer using ONLY the retrieved context.
        - If the answer is not found in the context, clearly say that you do not have enough information.
        - Do NOT invent facts.
        - Do NOT hallucinate numbers or financial rules.
        - Do NOT mention "context", "documents" or system logic.
        - Do NOT say "Based on the provided information".
        
        Behavior rules:
        - For theoretical questions → explain clearly + give a simple example.
        - For practical questions → give structured guidance.
        - Keep answers concise but meaningful.
        
        Style:
        - Friendly but professional.
        - Clear and structured.
        - Educational and practical.
        - Natural, not robotic.
        - Avoid excessive formal language.
        - Use short paragraphs if explanation is long.
        - Use bullet points only when helpful.
        """;

    @Override
    public List<RequiredParam> requiredParams() {
        return List.of();
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params) {
        return AssistantExecutionResult.error(
                intent(),
                "CONVERSATION_REQUIRED"
        );
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params, UUID conversationId) {
        try {
            String userQuestion =
                    (String) params.getOrDefault("originalMessage", "");

            if (userQuestion == null || userQuestion.isBlank()) {
                return AssistantExecutionResult.error(
                        intent(),
                        "EMPTY_QUESTION"
                );
            }

            String answer = aiModelService.generateWithRag(
                    SYSTEM_PROMPT,
                    userQuestion,
                    conversationId.toString()
            );

            return AssistantExecutionResult.success(
                    intent(),
                    new FinalResponseData(answer)
            );

        } catch (Exception e) {
            log.error("RAG generation failed", e);

            return AssistantExecutionResult.error(
                    intent(),
                    "KNOWLEDGE_GENERATION_FAILED"
            );
        }
    }
}
