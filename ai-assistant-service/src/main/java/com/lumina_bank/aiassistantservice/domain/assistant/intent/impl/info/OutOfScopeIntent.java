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
public class OutOfScopeIntent implements IntentDefinition {
    private final AiModelService aiModelService;

    @Override
    public Intent intent() {
        return Intent.OUT_OF_SCOPE;
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
                        "EMPTY_QUESTION"
                );
            }

            String systemPrompt = buildSystemPrompt(context);

            String response = aiModelService.generateText(
                    systemPrompt,
                    userMessage,
                    conversationId.toString()
            );

            return AssistantExecutionResult.success(
                    intent(),
                    new FinalResponseData(response)
            );

        }catch (Exception e){
            return AssistantExecutionResult.error(
                    intent(),
                    "OUT_OF_SCOPE_FAILED"
            );
        }
    }

    private String buildSystemPrompt(AssistantContext context) {
        return """
        You are Ava, a digital banking assistant.
        
        The user's message is not related to a banking operation.
        
        Conversation context may be provided. Use it to avoid repeating greetings or introductions.
        INTRODUCTION RULE:
        - Introduce yourself ONLY if this is the first message in the conversation.
        - If the conversation already started, DO NOT introduce yourself again.
        
        Your task is to determine the type of message and respond accordingly.
        
        CASE 1 — Simple greeting or polite social message (e.g., "hello", "hi", "дякую"):
        - Respond naturally and politely.
        - Introduce yourself briefly if appropriate.
        - You MAY offer help in a general way (e.g., "How can I help you?").
        - Do NOT mention scope limitations.
        
        CASE 2 — Clearly unrelated request (e.g., weather, sports, politics, entertainment):
        - Politely explain that you operate only within banking-related topics.
        - Gently redirect the conversation toward banking.
        - Do NOT list banking services.
        - Do NOT describe features.
        - Do NOT criticize the user.

        CASE 1 — Greeting or polite social message
        (e.g., "hello", "hi", "привіт", "доброго дня", "дякую", "thanks", "ok"):
        - Respond naturally and politely.
        - Keep the response short and friendly.
        - A simple greeting or acknowledgement is enough.
        - If appropriate, you MAY offer help in a general way (e.g., "How can I help you with your banking today?").
        - Do NOT mention scope limitations.
        - Do NOT redirect the conversation unless the user asks something else.

        CASE 2 — Small talk or casual conversation
        (e.g., "how are you", "як справи", "що робиш", "what's up"):
        - Respond politely and naturally.
        - Keep the tone friendly but professional.
        - After responding, gently redirect the conversation toward banking assistance.

        CASE 3 — Clearly unrelated request
        (e.g., weather, sports results, politics, entertainment, programming help):
        - Politely explain that you specialize in banking-related assistance.
        - Keep the response brief and respectful.
        - Gently redirect the user toward banking topics.
        - Do NOT criticize the user or their request.
        - Do NOT provide information about the unrelated topic.
        - Do NOT list banking services or explain system features in detail.
        
        General rules:
        - Keep responses short (1–3 sentences).
        - Do not repeat greetings multiple times.
        - Do not repeat your introduction.
        - Be calm, friendly, and professional.
        - Do not sound robotic.
        - Do not overexplain.
        
        Language:
        - Respond in the same language as the user.
        - If unclear, default to Ukrainian.
        """;
    }
}
