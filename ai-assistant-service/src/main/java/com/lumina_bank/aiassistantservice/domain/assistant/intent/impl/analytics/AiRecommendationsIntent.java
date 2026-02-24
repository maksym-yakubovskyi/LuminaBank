package com.lumina_bank.aiassistantservice.domain.assistant.intent.impl.analytics;

import com.lumina_bank.aiassistantservice.application.ai.port.AiModelService;
import com.lumina_bank.aiassistantservice.domain.assistant.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.assistant.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.assistant.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.info.FinalResponseData;
import com.lumina_bank.aiassistantservice.infrastructure.external.analytics.FeignAnalyticsGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiRecommendationsIntent implements IntentDefinition {
    private final FeignAnalyticsGateway analyticsGateway;
    private final AiModelService aiModelService;

    @Override
    public Intent intent() {
        return Intent.ANALYTICS_RECOMMENDATIONS;
    }

    @Override
    public List<RequiredParam> requiredParams(AssistantContext context) {
        return List.of();
    }

    @Override
    public AssistantExecutionResult execute(
            Map<String, Object> params,
            UUID conversationId,
            AssistantContext context
    ) {
        try {
            String userMessage =
                    (String) params.getOrDefault("originalMessage", "");

            if (userMessage == null || userMessage.isBlank()) {
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

            String systemPrompt = buildSystemPrompt(context);

            String userPrompt = buildUserPrompt(
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
                    systemPrompt,
                    userPrompt,
                    context.role(),
                    conversationId.toString()
            );

            return AssistantExecutionResult.success(
                    intent(),
                    new FinalResponseData(aiResponse)

            );
        } catch (ServiceCallException e) {
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

    private String buildSystemPrompt(AssistantContext context) {

        String roleContext = context.isBusiness()
                ? """
                  User profile:
                  - This is a BUSINESS client.
                  - Income represents business revenue.
                  - Expenses represent operational costs.
                  - Focus on profitability, liquidity and sustainability.
                  - Consider structural financial efficiency.
                  """
                : """
                  User profile:
                  - This is an INDIVIDUAL client.
                  - Income represents personal earnings.
                  - Expenses represent personal spending.
                  - Focus on budgeting, savings and financial safety.
                  - Emphasize long-term financial stability.
                  """;

        String roleStyle = context.isBusiness()
                ? """
                  Style:
                  - Professional and strategic.
                  - Structured and analytical.
                  - Focus on financial optimization.
                  - Avoid emotional or motivational tone.
                  - Use bullet points.
                  - Keep insights concise and actionable.
                  """
                : """
                  Style:
                  - Friendly but professional.
                  - Clear and supportive.
                  - Practical and realistic.
                  - Avoid overly technical language.
                  - Use bullet points.
                  - Keep response concise but meaningful.
                  """;

        return """
            You are Ava, an intelligent financial advisor
            inside a digital banking application.

            Identity:
            - Your name is Ava.
            - You are analytical, realistic and helpful.

            Language rules:
            - Detect the language of the user's message.
            - Always respond in the SAME language as the user.
            - If the language is unclear → default to Ukrainian.

            Core task:
            - Analyze the provided financial profile.
            - Carefully consider the user's specific question.
            - Use relevant financial education context.
            - Base conclusions ONLY on provided financial data.
            - Do NOT invent numbers.
            - Do NOT assume missing details.
            - Do NOT exaggerate risks.

            %s

            Output requirements:
            - Provide exactly 3 personalized financial recommendations.
            - Provide 1 positive reinforcement insight.
            - If imbalance detected → clearly explain why.
            - If stable → acknowledge stability.
            - Keep advice specific and actionable.
            - Avoid generic advice.

            %s

            Never mention internal calculations or technical structures.
            """.formatted(roleContext, roleStyle);
    }

    private String buildUserPrompt(
            String question,
            BigDecimal income,
            BigDecimal expense,
            BigDecimal cashFlow,
            BigDecimal expenseGrowth,
            BigDecimal incomeGrowth,
            Integer transactionCount,
            BigDecimal avgTransaction,
            String categories,
            String recipients
    ) {

        return """
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
        """.formatted(
                question,
                income,
                expense,
                cashFlow,
                expenseGrowth,
                incomeGrowth,
                transactionCount,
                avgTransaction,
                categories,
                recipients
        );
    }
}