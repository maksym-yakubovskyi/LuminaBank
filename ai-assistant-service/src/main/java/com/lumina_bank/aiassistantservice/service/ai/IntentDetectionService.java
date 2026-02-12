package com.lumina_bank.aiassistantservice.service.ai;

import com.lumina_bank.aiassistantservice.ai.AiModelService;
import com.lumina_bank.aiassistantservice.domain.result.IntentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IntentDetectionService {

    private final AiModelService ai;

    public IntentResult detect(String message) {

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
        - Classify ONLY the intent.
        - Do NOT extract parameters.
        - Return ONLY valid JSON.
        
        If the message is ONLY a short confirmation like:
        - yes
        - no
        - ok
        - 1000
        - debit
        - visa
        return intent = UNKNOWN.
        
        If the message clearly starts a new action,
        even if short, classify it properly.

        Possible intents:
        - CHECK_BALANCE
        - CREATE_ACCOUNT
        - CREATE_CARD
        - LIST_ACCOUNTS
        - LIST_CARDS
        - LIST_ACCOUNT_CARDS
        - UNKNOWN
        
        Indicating how confident you are in the intent classification between 0.0 and 1.0.
        """;

        System.out.println("IntentDetectionService : systemPrompt = " + systemPrompt + "\n userPrompt = " + message );

        IntentResult result = ai.generateEntity(
                systemPrompt,
                message,
                "intent-detection",
                IntentResult.class
        );

        System.out.println("IntentDetectionService : result = " + result);


        return result;
    }
}
