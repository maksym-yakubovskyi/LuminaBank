package com.lumina_bank.aiassistantservice.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumina_bank.aiassistantservice.ai.AiModelService;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResponseGenerationService {
    private final AiModelService ai;
    private final ObjectMapper objectMapper;

    public String generateResponse(
            String userMessage,
            AssistantExecutionResult result,
            UUID conversationId
    ) {

        StackTraceElement[] stack = Thread.currentThread().getStackTrace();

        if (stack.length > 2) {
            StackTraceElement caller = stack[2];
            System.out.println("ResponseGenerationService called from: "
                    + caller.getClassName() + "."
                    + caller.getMethodName());
        }

        String systemPrompt = """
            You are Ava, a banking AI assistant.

            Rules:
            - Use ONLY the provided data.
            - Never invent numbers.
            - Be concise and clear.
            - If clarification is needed, ask a clear follow-up question.
            - If an error occurred, politely explain it and ask a clarifying question.
            - Always offer a next step.
            - Respond in Ukrainian.
            - Do not guess.
            - Ask ONLY for missing parameters
            - If choices are provided, show them as a list
            - Ask ONE question at a time
            """;

        String userPrompt = """
            User message:
            "%s"

            Execution result:
            Status: %s
            Intent: %s
            Data (JSON): %s
            Error: %s
            
            Instructions:
            - If status is SUCCESS:
                - Answer the user question directly.
                - Do NOT describe all available data unless explicitly requested.
            - If status is NEED_CLARIFICATION:
                - Ask ONE short clarifying question.
            - If status is ERROR:
                - Do NOT mention errors or failures.
                - Explain the situation in a user-friendly way WITHOUT technical details
                - Ask a clarifying or alternative question.
            """.formatted(
                userMessage,
                result.status(),
                result.intent(),
                safeJson(result.data()),
                result.errorMessage()
        );

        System.out.println("ResponseGenerationService : systemPrompt = " + systemPrompt + "\n userPrompt = " + userPrompt );

        String res = ai.generateText(
                systemPrompt,
                userPrompt,
                conversationId.toString()
        );

        System.out.println("ResponseGenerationService : result = " + res);

        return res;
    }

    private String safeJson(Object data) {
        if (data == null) return "null";
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            return "\"<serialization_error>\"";
        }
    }
}
