package com.lumina_bank.aiassistantservice.service.ai;

import com.lumina_bank.aiassistantservice.ai.AiModelService;
import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
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


    public Map<String, Object> extract(
            String message,
            Intent intent,
            List<RequiredParam> schema,
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
        You extract parameters from a user message for a banking intent.

        Rules:
        - Extract ONLY parameters defined in schema
        - If a value is expressed in another language,
          in adjective form,
          with spelling mistakes,
          or in a semantically equivalent way,
          map it to the closest matching ENUM option.
        - Perform semantic normalization.
        - Use linguistic understanding.
        - Do not invent new enum values.
        - If no reasonable match exists → omit.
        - Return ONLY a flat JSON object.
        - Include ONLY keys defined in the schema.
        - Return ONLY key-value pairs for extracted parameters.
            Do not return intent.
            Do not return missingParams.
            Do not return explanations.
            Do not return clarifyingQuestion.
        """;

        String userPrompt = """
        Intent: %s
        Message: "%s"
        Schema:
        %s
        """.formatted(
                intent,
                message,
                schema
        );


        try {
            System.out.println("ParameterExtractionService : systemPrompt = " + systemPrompt + "\n userPrompt = " + userPrompt );

            Map<String, Object> result = ai.generateEntity(
                    systemPrompt,
                    userPrompt,
                    conversationId.toString(),
                    MAP_TYPE
            );

//            result.keySet().removeIf(
//                    key -> schema.stream().noneMatch(p -> p.name().equals(key))
//            );

            System.out.println("ParameterExtractionService : result = " + result);

            return result;

        } catch (Exception e) {
            return Map.of();
        }
    }
}
