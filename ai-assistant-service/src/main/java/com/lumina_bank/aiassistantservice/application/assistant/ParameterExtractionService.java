package com.lumina_bank.aiassistantservice.application.assistant;

import com.lumina_bank.aiassistantservice.application.ai.port.AiModelService;
import com.lumina_bank.aiassistantservice.domain.assistant.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.application.assistant.params.ParamsJsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParameterExtractionService  {

    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE =
            new ParameterizedTypeReference<>() {};

    private final AiModelService ai;
    private final ParamsJsonMapper paramsJsonMapper;

    public Map<String, Object> extract(
            String message,
            Intent intent,
            List<RequiredParam> schema,
            String awaitingParam,
            UUID conversationId
    ) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();

        if (stack.length > 2) {
            StackTraceElement caller = stack[2];
            System.out.println("ParameterExtractionService called from: "
                    + caller.getClassName() + "."
                    + caller.getMethodName());
        }

        String systemPrompt = """
        You are a strict parameter extraction engine for a banking assistant.
        
        Your task:
        Extract structured parameters from a user message according to the provided schema.
        
        CRITICAL RULES:
        - Extract ONLY parameters explicitly defined in the schema.
        - Never invent parameters.
        - Never invent enum values.
        - If no valid value can be confidently determined → omit the parameter.
        - Return ONLY a flat JSON object.
        - Do NOT return explanations.
        - Do NOT return missing parameters.
        - Do NOT return intent.
        - Do NOT return null values.
        - If nothing is extracted → return {}.
        
        PARAM TYPES RULES:
        
        ENUM:
        - Match semantically.
        - Support:
          - synonyms
          - adjective forms
          - spelling mistakes
          - different languages variants
        - Map to EXACT enum option from schema.
        - If schema contains options list, only use values from that list.
        - If user refers by ordinal position (first, second, третій),
          and schema contains ordered options,
          map ordinal to corresponding option value.
        
        NUMBER:
        - Extract numeric value only.
        - Ignore currency words.
        - Convert textual numbers to digits if clear.
        - If ordinal word (first, second) and schema has options,
          map ordinal to corresponding option numeric id.
        - Return as number (not string).
        
        STRING:
        - Extract exact meaningful phrase.
        - Trim spaces.
        - Do not modify content unless normalization required.
        - If the user message consists of a short free-form phrase (1–3 words)
          and a STRING parameter exists in the schema,
          treat the entire message as the value of that parameter.
        
        DATE:
        - Normalize to YYYY-MM-DD.
        - Support:
          - today
          - yesterday
          - tomorrow
          - dd.MM.yyyy
          - dd-MM-yyyy
          - ISO format
        - If invalid → omit.
        
        YEAR_MONTH:
        - Normalize to YYYY-MM.
        - If only month is provided → use current year.
        - Support month names in different languages.
        - Support formats:
          - MM.yyyy
          - MM-yyyy
          - yyyy-MM
          - yyyy/MM
        - explicit month names (January, лютий, март, etc.)
        - numeric formats (MM.yyyy, yyyy-MM, etc.)
        - relative expressions
        Relative expressions:
        - "this month" / "цей місяць" → current month
        - "last month" / "минулий місяць" → previous month
        - "next month" → next month
        
        CONTEXT SELECTION RULE:
        If the assistant previously asked for a specific parameter,
        treat the user's reply as the value of that parameter.
        If the assistant message contains a numbered or bulleted list of options,
        the user may respond with:
        - ordinal words (first, second, перший, другий)
        - the number of the option
        - partial identifier
        - description
        You must map the user response to the correct value from the assistant message list.

        CLARIFICATION PRIORITY RULE:
        If the assistant previously asked the user to provide a specific parameter,
        the user's reply MUST be interpreted as the value of that parameter.
        This rule has higher priority than all other extraction rules.
        Do not attempt to map the message to other parameters.

        OUTPUT FORMAT:
        Return ONLY valid JSON.
        Example:
        {
          "accountId": 2,
          "yearMonth": "2024-10"
        }
        """;

        String userPrompt = """
        Intent: %s
        
        Message: "%s"
        
        CURRENT PARAMETER:
        %s
        
        Schema(JSON format):
        %s
        """.formatted(
                intent,
                message,
                awaitingParam,
                paramsJsonMapper.toJsonSchema(schema)
        );

        try {
            System.out.println("ParameterExtractionService : systemPrompt = " + systemPrompt + "\n userPrompt = " + userPrompt );

            Map<String, Object> result = ai.generateEntity(
                    systemPrompt,
                    userPrompt,
                    conversationId.toString(),
                    MAP_TYPE
            );

            System.out.println("ParameterExtractionService : result = " + result);

            return result;

        } catch (Exception e) {
            return Map.of();
        }
    }
}
