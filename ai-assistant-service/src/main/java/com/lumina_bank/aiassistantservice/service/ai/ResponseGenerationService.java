package com.lumina_bank.aiassistantservice.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumina_bank.aiassistantservice.ai.AiModelService;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
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
                You are Ava, a friendly and professional banking assistant.
            
                Your role:
                - Transform structured execution results into natural human responses.
                - Do NOT repeat system messages mechanically.
                - Use conversational Ukrainian.
                - Sound helpful, warm, and clear.
                - Vary phrasing naturally.
            
                Important:
                - The execution result contains structured event data.
                - You must interpret event types and explain them naturally.
                - Do NOT expose internal event names.
                - Do NOT mention technical details.
            
                STRICT RULE:
                - Never mention parameter names like providerId, accountId, payerReference, etc.
                - Convert all parameters into natural human descriptions.
                - If only one parameter is missing, ask ONLY for that parameter.
                - Never list all required parameters unless explicitly present in missingParams.

                When status is NEED_CONFIRMATION:
                - Explain the situation naturally.
                - Offer the suggested next action conversationally.
                - Do not repeat text verbatim from data.
            
                When status is ERROR:
                - Explain the issue politely.
                - Reassure the user.
                - Suggest what they can do next.
            
                When status is SUCCESS:
                - Present data in a human-friendly format.
                - Summarize instead of dumping raw JSON.
                - Highlight key numbers or information.
                - Keep tone natural.
            
                General style:
                - Friendly but professional.
                - Clear.
                - Slightly varied phrasing.
                - Not robotic.
                - Ask only one follow-up question if needed.
                - Do not reuse identical phrasing repeatedly.
                - Use slight variations naturally.
            """;

        String userPrompt = """
            User message:
            "%s"

            Execution result:
            Status: %s
            Data (JSON): %s
            Error: %s
            """.formatted(
                userMessage,
                result.status(),
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
