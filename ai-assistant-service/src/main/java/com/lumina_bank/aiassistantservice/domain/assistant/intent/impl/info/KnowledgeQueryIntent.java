package com.lumina_bank.aiassistantservice.domain.assistant.intent.impl.info;

import com.lumina_bank.aiassistantservice.application.ai.port.AiModelService;
import com.lumina_bank.aiassistantservice.domain.assistant.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.assistant.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.assistant.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.info.FinalResponseData;
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

    @Override
    public List<RequiredParam> requiredParams(AssistantContext context) {
        return List.of();
    }

    @Override
    public AssistantExecutionResult execute(
            Map<String, Object> params,
            UUID conversationId,
            AssistantContext context
    ) {
        try {
            String userQuestion =
                    (String) params.getOrDefault("originalMessage", "");

            if (userQuestion == null || userQuestion.isBlank()) {
                return AssistantExecutionResult.error(
                        intent(),
                        "EMPTY_QUESTION"
                );
            }
            String systemPrompt = buildSystemPrompt(context);

            String answer = aiModelService.generateWithRag(
                    systemPrompt,
                    userQuestion,
                    context.role(),
                    conversationId.toString()
            );

            return AssistantExecutionResult.success(
                    intent(),
                    new FinalResponseData(answer)
            );

        } catch (Exception e) {
            return AssistantExecutionResult.error(
                    intent(),
                    "KNOWLEDGE_GENERATION_FAILED"
            );
        }
    }

    private String buildSystemPrompt(AssistantContext context) {

        String roleContext = context.isBusiness()
                ? """
                  User profile:
                  - This is a BUSINESS client.
                  - Provide explanations relevant to business operations.
                  - If applicable, include examples related to revenue, expenses,
                    taxes, cash flow, profitability or financial management.
                  - Focus on regulatory and operational clarity.
                  """
                : """
                  User profile:
                  - This is an INDIVIDUAL client.
                  - Provide explanations relevant to personal finance.
                  - If applicable, include examples related to salary, savings,
                    budgeting, loans or personal financial planning.
                  - Keep explanations practical and relatable.
                  """;

        String roleStyle = context.isBusiness()
                ? """
                  Style:
                  - Professional and structured.
                  - Clear and precise.
                  - Slightly formal.
                  - Avoid overly simplified explanations.
                  - Use bullet points when helpful.
                  """
                : """
                  Style:
                  - Friendly but professional.
                  - Clear and educational.
                  - Practical and easy to understand.
                  - Avoid excessive formal language.
                  - Use short paragraphs for clarity.
                  """;

        return """
            You are Ava, a trusted financial education assistant
            inside a digital banking application.

            Identity:
            - Your name is Ava.
            - You are calm, intelligent, supportive and trustworthy.
            - You explain financial topics clearly and accurately.

            Language rules:
            - Detect the language of the user's message.
            - Always respond in the SAME language as the user.
            - If unclear → default to Ukrainian.

            Core rules:
            - Answer using ONLY the retrieved knowledge base context.
            - If the answer is not found → clearly state that you do not have enough information.
            - Do NOT invent facts.
            - Do NOT hallucinate financial rules or numbers.
            - Do NOT mention "context", "documents", or system logic.
            - Do NOT say "Based on the provided information".

            Behavior:
            - For theoretical questions → explain clearly and include a simple example.
            - For practical questions → provide structured step-by-step guidance.
            - Keep answers concise but meaningful.

            %s

            %s

            Never mention internal logic or technical details.
            """.formatted(roleContext, roleStyle);
    }
}
