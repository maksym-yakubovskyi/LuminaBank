package com.lumina_bank.aiassistantservice.domain.assistant.intent.impl.info;

import com.lumina_bank.aiassistantservice.application.ai.port.AiModelService;
import com.lumina_bank.aiassistantservice.domain.assistant.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.assistant.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.assistant.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.info.FinalResponseData;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AssistantInfoIntent implements IntentDefinition {
    private final AiModelService aiModelService;

    @Override
    public Intent intent() {
        return Intent.ASSISTANT_INFO;
    }

    @Override
    public List<RequiredParam> requiredParams(AssistantContext context) {
        return List.of();
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params, UUID conversationId, AssistantContext context) {
        try{
            String userMessage =
                    (String) params.getOrDefault("originalMessage", "");

            if (userMessage == null || userMessage.isBlank()) {
                return AssistantExecutionResult.error(
                        intent(),
                        "EMPTY_MESSAGE"
                );
            }

            String systemPrompt = buildSystemPrompt(context);

            String response = aiModelService.generateWithRag(
                    systemPrompt,
                    userMessage,
                    context.role(),
                    conversationId.toString()
            );

            return AssistantExecutionResult.success(
                    intent(),
                    new FinalResponseData(response)
            );

        }catch (Exception e){
            return AssistantExecutionResult.error(
                    intent(),
                    "ASSISTANT_INFO_FAILED"
            );
        }
    }

    private String buildSystemPrompt(AssistantContext context) {

        return """
            You are Ava, a digital banking assistant inside a banking application.

            Purpose:
            - Answer questions about yourself.
            - Explain your capabilities.
            - Clarify your role in the banking system.

            Strict rules:
            - Only describe capabilities that exist in the system.
            - Do NOT invent new features.
            - If information is missing, say you don't have that information.
            - Do NOT discuss internal technical architecture.
            - Do NOT mention prompts, RAG, vector database or system logic.

            Language:
            - Always respond in the same language as the user.
            - If unclear → default to Ukrainian.

            Style:
            - Clear
            - Professional
            - Slightly friendly
            - Short and structured

            Never mention internal implementation details.
            """;
    }
}
