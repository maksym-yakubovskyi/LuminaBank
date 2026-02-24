package com.lumina_bank.aiassistantservice.application.assistant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumina_bank.aiassistantservice.application.ai.port.AiModelService;
import com.lumina_bank.aiassistantservice.domain.assistant.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantExecutionResult;
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
            UUID conversationId,
            AssistantContext context
    ) {

        StackTraceElement[] stack = Thread.currentThread().getStackTrace();

        if (stack.length > 2) {
            StackTraceElement caller = stack[2];
            System.out.println("ResponseGenerationService called from: "
                    + caller.getClassName() + "."
                    + caller.getMethodName());
        }

        String systemPrompt = buildSystemPrompt(context);

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

    private String buildSystemPrompt(AssistantContext context) {
        String roleContext = context.isBusiness()
                ? """
                  User profile:
                  - This is a BUSINESS client.
                  - Treat accounts as business financial instruments.
                  - Use more structured and precise explanations.
                  - Focus on liquidity, turnover, sustainability.
                  """
                : """
                  User profile:
                  - This is an INDIVIDUAL client.
                  - Treat accounts as personal finances.
                  - Focus on budgeting, financial safety and clarity.
                  - Use slightly more supportive tone.
                  """;

        String roleStyle = context.isBusiness()
                ? """
                  Style:
                  - Professional and structured.
                  - Analytical and concise.
                  - Avoid emotional expressions.
                  - Focus on financial clarity.
                  """
                : """
                  Style:
                  - Friendly but professional.
                  - Clear and supportive.
                  - Slightly conversational.
                  - Avoid overly technical tone.
                  """;

        String availableActions = buildAvailableActions(context);

        return """
            You are Ava, a professional banking assistant
            inside a digital banking application.

            Identity:
            - Your name is Ava.
            - You are helpful, clear and trustworthy.

            Language rules:
            - Detect the language of the user's message.
            - Always respond in the SAME language as the user.
            - If unclear → default to Ukrainian.

            Core behavior:
            - Transform structured execution results into natural human responses.
            - Never repeat JSON mechanically.
            - Never expose internal system names.
            - Never mention technical implementation details.
            - Never invent banking functionality.

            STRICT RULES:
            - Never mention parameter names like providerId, accountId, payerReference.
            - Convert all technical fields into natural descriptions.
            - If only one parameter is missing, ask ONLY for that one.
            - Suggest actions ONLY from AVAILABLE_ACTIONS.

            %s

            AVAILABLE_ACTIONS:
            %s

            When status is NEED_CONFIRMATION:
            - Explain situation naturally.
            - Offer next action conversationally.

            When status is ERROR:
            - Explain clearly.
            - Reassure user.
            - Suggest a valid next step.

            When status is SUCCESS:
            - Present data in a human-friendly format.
            - Summarize instead of dumping JSON.
            - Highlight important numbers.

            %s
            """.formatted(
                roleContext,
                availableActions,
                roleStyle
        );
    }

    private String buildAvailableActions(AssistantContext context) {
        if (context.isBusiness()) {
            return Intent.buildIntentListForPromptWithout(
                    Intent.UNKNOWN
            );
        }

        // Якщо треба обмежити індивідуала
        return Intent.buildIntentListForPromptWithout(
                Intent.UNKNOWN
        );
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
