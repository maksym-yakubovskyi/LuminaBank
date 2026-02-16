package com.lumina_bank.aiassistantservice.domain.intent.impl.analytics;

import com.lumina_bank.aiassistantservice.ai.AiModelService;
import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.data.analytics.AiRecommendationsData;
import com.lumina_bank.aiassistantservice.service.client.analytics.FeignAnalyticsGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiRecommendationsIntent implements IntentDefinition {
    private final FeignAnalyticsGateway analyticsGateway;
    private final AiModelService aiModelService;

    private static final String ANALYTICS_PROMPT_SYSTEM = """
        You are a professional financial advisor.
        
        Analyze the user's financial profile and generate:
        
        - 3 personalized financial recommendations
        - 1 positive reinforcement insight
        - Keep it concise and practical
        - Do NOT invent numbers
        - Use only the provided data
        - Do NOT mention percentages unless provided
        
        Return clear bullet points.
        """;

    private static final String ANALYTICS_PROMPT_USER= """
        Financial profile:
        
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
    public AssistantExecutionResult execute(Map<String, Object> params) {
        try{
            var rec = analyticsGateway.getRecommendationInfo();

            String categoriesBlock = rec.topCategories().stream()
                    .map(c -> "- " + c.category()
                            + ": " + c.percentage() + "%")
                    .reduce("", (a, b) -> a + b + "\n");

            String recipientsBlock = rec.topRecipients().stream()
                    .map(r -> "- " + r.displayName()
                            + " (" + r.transactionCount() + " transactions)")
                    .reduce("", (a, b) -> a + b + "\n");

            String prompt = ANALYTICS_PROMPT_USER.formatted(
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

            String aiResponse = aiModelService.generateText(
                    ANALYTICS_PROMPT_SYSTEM,prompt,"analytics-recomendation"
            );

            return AssistantExecutionResult.success(
                    intent(),
                    new AiRecommendationsData(aiResponse,prompt)
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
}
