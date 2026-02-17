package com.lumina_bank.aiassistantservice.domain.intent.impl.analytics;

import com.lumina_bank.aiassistantservice.ai.AiModelService;
import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.data.info.FinalResponseData;
import com.lumina_bank.aiassistantservice.service.client.analytics.FeignAnalyticsGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiRecommendationsIntent implements IntentDefinition {
    private final FeignAnalyticsGateway analyticsGateway;
    private final AiModelService aiModelService;

    private static final String SYSTEM_PROMPT = """
    You are Ava, a professional and intelligent financial advisor
    inside a digital banking application.

    Identity:
    - Your name is Ava.
    - You are supportive, analytical and realistic.

    Language rules:
    - Detect the language of the user's message.
    - Always respond in the SAME language as the user.
    - If the language is unclear → default to Ukrainian.

    Core task:
    - Analyze the provided financial profile.
    - Carefully consider the user's specific question.
    - Use financial education context if relevant.
    - Generate practical, realistic recommendations.
    - Base conclusions ONLY on the provided financial data.
    - Do NOT invent numbers.
    - Do NOT assume missing financial details.
    - Do NOT exaggerate risks.
    - If the user's question focuses on a specific area (e.g., investing, budgeting, saving), prioritize recommendations in that area.

    Output requirements:
    - Provide exactly 3 personalized financial recommendations.
    - Provide 1 positive reinforcement insight.
    - If financial imbalance is detected, clearly explain why.
    - If financial situation is stable, acknowledge it.
    - Keep advice actionable and specific.
    - Avoid generic advice like "save more money" without context.

    Style:
    - Friendly but professional.
    - Clear and structured.
    - Analytical but human.
    - Avoid robotic tone.
    - Avoid repeating numbers excessively.
    - Use bullet points for recommendations.
    - Keep response concise but meaningful.

    Never mention internal data structures or technical details.
    """;

    private static final String USER_TEMPLATE = """
        USER QUESTION:
        %s

        FINANCIAL PROFILE:

        Average monthly income: %s
        Average monthly expense: %s
        Average monthly cash flow: %s

        Expense growth rate: %s%%
        Income growth rate: %s%%

        Monthly transaction count: %s
        Average transaction amount: %s

        Top spending categories (last 6 months):
        %s

        Top recipients (last 6 months):
        %s
        """;

    @Override
    public Intent intent() {
        return Intent.ANALYTICS_RECOMMENDATIONS;
    }

    @Override
    public List<RequiredParam> requiredParams() {
        return List.of();
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params, UUID conversationId) {
        try{
            String userMessage  =
                    (String) params.getOrDefault("originalMessage", "");

            if (userMessage  == null || userMessage .isBlank()) {
                return AssistantExecutionResult.error(
                        intent(),
                        "EMPTY_MESSAGE"
                );
            }

            var rec = analyticsGateway.getRecommendationInfo();

            String categoriesBlock = rec.topCategories().stream()
                    .map(c -> "- " + c.category() + ": " + c.percentage() + "%")
                    .reduce("", (a, b) -> a + b + "\n");

            String recipientsBlock = rec.topRecipients().stream()
                    .map(r -> "- " + r.displayName()
                            + " (" + r.transactionCount() + " transactions)")
                    .reduce("", (a, b) -> a + b + "\n");

            String userPrompt = USER_TEMPLATE.formatted(
                    userMessage,
                    rec.avgMonthlyIncome(),
                    rec.avgMonthlyExpense(),
                    rec.avgMonthlyCashFlow(),
                    rec.expenseGrowthPercent(),
                    rec.incomeGrowthPercent(),
                    rec.monthlyTransactionCount(),
                    rec.avgTransactionAmount(),
                    categoriesBlock,
                    recipientsBlock
            );

            String aiResponse = aiModelService.generateWithRag(
                    SYSTEM_PROMPT,
                    userPrompt,
                    conversationId.toString()
            );

            return AssistantExecutionResult.success(
                    intent(),
                    new FinalResponseData(aiResponse)

            );
        }catch (ServiceCallException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage()
            );
        } catch (Exception e) {
            return AssistantExecutionResult.error(
                    intent(),
                    "AI_GENERATION_FAILED");
        }
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params) {
        return AssistantExecutionResult.error(
                intent(),
                "CONVERSATION_REQUIRED"
        );
    }
}
