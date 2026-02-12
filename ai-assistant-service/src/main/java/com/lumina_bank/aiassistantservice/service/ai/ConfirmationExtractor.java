package com.lumina_bank.aiassistantservice.service.ai;

import com.lumina_bank.aiassistantservice.ai.AiModelService;
import com.lumina_bank.aiassistantservice.domain.enums.ConfirmationDecision;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfirmationExtractor {

    private final AiModelService ai;

    public boolean extractConfirmation(String message, UUID conversationId) {

        StackTraceElement[] stack = Thread.currentThread().getStackTrace();

        if (stack.length > 2) {
            StackTraceElement caller = stack[2];
            System.out.println("ConfirmationExtractor called from: "
                    + caller.getClassName() + "."
                    + caller.getMethodName());
        }

        String systemPrompt = """
                You determine whether the user CONFIRMS or DECLINES an action.
                
                Rules:
                - Return ONLY valid JSON
                - confirmed: true or false
                - Do NOT explain
                """;

        String userPrompt = """
                User message:
                "%s"
                """.formatted(message);

        record Confirmation(boolean confirmed) {
        }

        System.out.println("ConfirmationExtractor : systemPrompt = " + systemPrompt + "\n userPrompt = " + userPrompt );

        Confirmation result = ai.generateEntity(
                systemPrompt,
                userPrompt,
                conversationId.toString(),
                Confirmation.class
        );

        System.out.println("ConfirmationExtractor : result = " + result);

        return result.confirmed();
    }

    public ConfirmationDecision extractDecision(String message, UUID conversationId){

        StackTraceElement[] stack = Thread.currentThread().getStackTrace();

        if (stack.length > 2) {
            StackTraceElement caller = stack[2];
            System.out.println("ConfirmationExtractor called from: "
                    + caller.getClassName() + "."
                    + caller.getMethodName());
        }

        String systemPrompt = """
            You determine the user's decision regarding a pending action.
            
            Return ONLY valid JSON with field:
            decision: CONFIRM | DECLINE | MODIFY
            
            CONFIRM  — user clearly agrees to proceed.
            DECLINE  — user cancels or refuses the action entirely.
            MODIFY   — user wants to change something or continue editing parameters.
            
            Examples:
            
            "так" → CONFIRM
            "ок" → CONFIRM
            "підтверджую" → CONFIRM
            
            "ні" → DECLINE
            "передумав" → DECLINE
            "скасувати" → DECLINE
            
            "давай іншу валюту" → MODIFY
            "не так, зробимо кредитний" → MODIFY
            "не правильна назва"
            """;


        String userPrompt = """
                User message:
                "%s"
                """.formatted(message);

        record ConfirmationResponse(ConfirmationDecision decision) {}

        System.out.println("ConfirmationExtractor : systemPrompt = " + systemPrompt + "\n userPrompt = " + userPrompt );

        ConfirmationResponse result = ai.generateEntity(
                systemPrompt,
                userPrompt,
                conversationId.toString(),
                ConfirmationResponse.class
        );

        System.out.println("ConfirmationExtractor : result = " + result);

        return result.decision();
    }
}