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

    public ConfirmationDecision extractDecision(String message, UUID conversationId){

        StackTraceElement[] stack = Thread.currentThread().getStackTrace();

        if (stack.length > 2) {
            StackTraceElement caller = stack[2];
            System.out.println("ConfirmationExtractor called from: "
                    + caller.getClassName() + "."
                    + caller.getMethodName());
        }

        String systemPrompt = """
            You are a strict confirmation classifier for a banking assistant.
            
            Your task:
            Classify the user's decision regarding a pending financial action.
            
            CRITICAL RULES:
            - Return ONLY valid JSON.
            - Return exactly one field:
              decision: CONFIRM | DECLINE | MODIFY | UNCERTAIN
            - Do NOT explain.
            - Do NOT add text.
            - If the message is ambiguous, unclear, or unrelated → return UNCERTAIN.
            
            Definitions:
            
            CONFIRM:
            - User clearly agrees.
            - Examples:
              "так"
              "ок"
              "підтверджую"
              "робимо"
              "yes"
              "go ahead"
            
            DECLINE:
            - User cancels or refuses.
            - Examples:
              "ні"
              "скасувати"
              "передумав"
              "не треба"
              "cancel"
            
            MODIFY:
            - User wants to change something.
            - Examples:
              "змініть суму"
              "не так"
              "давай іншу валюту"
              "не 1000 а 500"
            
            UNCERTAIN:
            - Hesitation
            - Question
            - Unclear intent
            - Any unrelated message
            """;


        String userPrompt = """
                User message:
                "%s"
                """.formatted(message);

        record ConfirmationResponse(ConfirmationDecision decision) {}

        System.out.println("ConfirmationExtractor : systemPrompt = " + systemPrompt + "\n userPrompt = " + userPrompt );
        try {
            ConfirmationResponse result = ai.generateEntity(
                    systemPrompt,
                    userPrompt,
                    conversationId.toString(),
                    ConfirmationResponse.class
            );
            if (result == null || result.decision() == null) {
                return ConfirmationDecision.UNCERTAIN;
            }

            System.out.println("ConfirmationExtractor : result = " + result);

            return result.decision();

        } catch (Exception e) {
            return ConfirmationDecision.UNCERTAIN;
        }
    }
}