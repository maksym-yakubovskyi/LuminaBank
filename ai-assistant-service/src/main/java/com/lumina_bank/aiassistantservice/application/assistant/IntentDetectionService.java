package com.lumina_bank.aiassistantservice.application.assistant;

import com.lumina_bank.aiassistantservice.application.ai.port.AiModelService;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.assistant.result.IntentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IntentDetectionService {

    private final AiModelService ai;

    public IntentResult detect(String message, UUID conversationId) {

        StackTraceElement[] stack = Thread.currentThread().getStackTrace();

        if (stack.length > 2) {
            StackTraceElement caller = stack[2];
            System.out.println("IntentDetectionService called from: "
                    + caller.getClassName() + "."
                    + caller.getMethodName());
        }

        String systemPrompt = """
        You are an intent classifier for a banking system.

        Rules:
        - Classify ONLY the user's intent.
        - Do NOT extract parameters.
        - Return ONLY valid JSON.
        
        If the message is ONLY a short confirmation(yes/no/ok/number/etc)
        return intent = UNKNOWN.
        
        If the message clearly starts a new action,
        even if short, classify it properly.
        
        If multiple intents seem possible, choose the most specific one.

        Available intents:
        "%s"
        
        Indicating how confident you are in the intent classification between 0.0 and 1.0.
        """.formatted(
                Intent.buildIntentListForPrompt()
        );

        System.out.println("IntentDetectionService : systemPrompt = " + systemPrompt + "\n userPrompt = " + message );

        IntentResult result = ai.generateEntity(
                systemPrompt,
                message,
                conversationId.toString(),
                IntentResult.class
        );

        System.out.println("IntentDetectionService : result = " + result);

        return result;
    }
}