package com.lumina_bank.aiassistantservice.application.assistant;

import com.lumina_bank.aiassistantservice.application.ai.port.AiModelService;
import com.lumina_bank.aiassistantservice.domain.assistant.result.LlmIntentResult;
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
        You are a high-precision intent classification engine for a banking AI assistant.

        Your task:
        Classify the user's LAST message into exactly ONE intent from the list below.
        You may use conversation context if it is provided.
        
        STRICT RULES:
        - Output ONLY valid JSON.
        - Do NOT explain anything.
        - Do NOT extract parameters.
        - Do NOT generate natural language.
        - Do NOT include any text outside the JSON object.
        - The "intent" must EXACTLY match one of the available intent names.
        
        CLASSIFICATION PRINCIPLES
        - Choose the MOST SPECIFIC intent.
        - If multiple intents appear, choose the single primary actionable intent.
        - If the message clearly starts a new action, classify it as a new intent.
        - If the message is short but clearly refers to a banking action (e.g. "balance", "loan", "card"), classify it normally.
        - Do not invent hidden meaning beyond what is reasonably implied.
        - If the user's message is a continuation of a previous question, classify according to the ongoing topic.
        
        MULTI-REQUEST RULE:
        If the user requests multiple banking operations in a single message,
        choose the intent that represents the FIRST requested action.
        
        SPECIAL CASES
        - If the message requests a banking operation → choose the corresponding operational intent.
        - If unsure but related to banking → choose the closest valid intent.
        
        IMPORTANT CONTEXT RULE
        Conversation context should only be used if the user's LAST message
        contains words that clearly continue the banking action.
        
        Short reactions, emotions, slang, confirmations, jokes, or comments
        (e.g. "ок", "понял", "ясно", "да ти что", "лол")
        DO NOT continue the banking operation.
        
        If the last message does not explicitly reference the banking action,
        classify it independently of the previous context.
        
        Available intents:
        "%s"
        
        CONFIDENCE RULES:
        0.90–1.00 → Clear and explicit intent
        0.70–0.89 → Strong but slightly implicit
        0.50–0.69 → Ambiguous but best match exists
        Below 0.50 → Unclear, weakly supported, or insufficient information to classify confidently
        """.formatted(
                Intent.buildIntentListForPrompt()
        );

        System.out.println("IntentDetectionService : systemPrompt = " + systemPrompt + "\n userPrompt = " + message );

        LlmIntentResult raw = ai.generateEntity(
                systemPrompt,
                message,
                conversationId.toString(),
                LlmIntentResult .class
        );

        Intent intent;

        System.out.println("IntentDetectionService : raw = " + raw);

        try {
            intent = Intent.valueOf(raw.intent());
        } catch (Exception e) {
            intent = Intent.UNKNOWN;
        }

        IntentResult result = new IntentResult(intent, raw.confidence());

        System.out.println("IntentDetectionService : result = " + result);

        return result;
    }
}