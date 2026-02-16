package com.lumina_bank.aiassistantservice.service.ai;

import com.lumina_bank.aiassistantservice.ai.AiModelService;
import com.lumina_bank.aiassistantservice.domain.result.IntentResult;
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
        - CHECK_BALANCE (Description: User wants to see account balance.)
        - CREATE_ACCOUNT (Description: User wants to open a new account.)
        - CREATE_CARD (Description: User wants to create a new card.)
        - LIST_ACCOUNTS (Description: User wants to see list of accounts or information about an account/accounts.)
        - LIST_CARDS (Description: User wants to see all cards or information about an card/cards.)
        - LIST_ACCOUNT_CARDS (Description: User wants to see cards of a specific account.)
        
        - USER_INFORMATION (Description: User wants to view their personal profile or information.)
        - UPDATE_USER_INFORMATION (Description: User wants to change or update their personal data.)
        
        - PAYMENT_HISTORY (Description: User wants transaction history for an account (not analytics).)
        - TRANSFER_BY_CARD (Description: User wants to transfer money to another user(card).)
        - PAYMENT_SERVICE_PROVIDER (Description: User wants to pay a service provider (utilities, mobile, internet, etc.).)
        - LIST_PAYMENT_TEMPLATES (Description: User wants to see saved payment templates.)
        - PAYMENT_BY_TEMPLATE (Description: User wants to make a payment using an existing template.)
        
        - ANALYTICS_MONTHLY (Description: User wants monthly income/expense summary.)
        - ANALYTICS_DAILY  (Description: User wants daily income/expense summary.)
        - ANALYTICS_BY_CATEGORY (Description: User wants spending grouped by categories.)
        - ANALYTICS_TOP_RECIPIENTS (Description: User wants most frequent or highest recipients.)
        - ANALYTICS_FORECAST (Description: User wants a prediction of future expenses, income, cash flow, financial trends, or upcoming financial outlook based on past activity.)
        - ANALYTICS_RECOMMENDATIONS (Description: User wants personalized financial advice, spending improvement suggestions, budgeting tips, or money management insights based on their financial activity.)
        
        - UNKNOWN (Description: Message does not match any intent.)
        
        Indicating how confident you are in the intent classification between 0.0 and 1.0.
        """;

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