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
public class ForecastAnalyticsIntent implements IntentDefinition {
    private final FeignAnalyticsGateway analyticsGateway;
    private final AiModelService aiModelService;

    @Override
    public Intent intent() {
        return Intent.ANALYTICS_FORECAST;
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
                        "EMPTY_QUESTION"
                );
            }

            var forecast = analyticsGateway.getForecast();

            String systemPrompt = buildSystemPrompt(context);

            String userPrompt  = buildUserPrompt(
                    userMessage,
                    forecast.predictedIncome(),
                    forecast.predictedExpense(),
                    forecast.predictedCashFlow(),
                    forecast.expenseTrendPercent(),
                    forecast.incomeTrendPercent(),
                    forecast.monthsAnalyzed()
            );

            String aiResponse = aiModelService.generateText(
                    systemPrompt,
                    userPrompt,
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
                    "AI_FORECAST_GENERATION_FAILED"
            );
        }
    }

    private String buildSystemPrompt(AssistantContext context) {
        String roleSpecificContext = context.isBusiness()
                ? """
              Context:
              - The user is a BUSINESS client.
              - Treat income as business revenue.
              - Treat expenses as operating costs.
              - Focus on liquidity, sustainability and financial stability.
              - If imbalance exists, explain operational risks.
              """
                : """
              Context:
              - The user is an INDIVIDUAL client.
              - Treat income as personal earnings.
              - Treat expenses as personal spending.
              - Focus on budgeting stability and financial safety.
              - If imbalance exists, explain impact on personal financial health.
              """;

        String roleSpecificStyle = context.isBusiness()
                ? """
              Style:
              - Professional and structured.
              - Analytical and concise.
              - Focus on financial stability and risk exposure.
              - Avoid emotional or motivational tone.
              - Use clear bullet points.
              - Keep insights practical and strategic.
              """
                : """
              Style:
              - Professional but friendly.
              - Clear and structured.
              - Supportive and calm.
              - Avoid dramatic language.
              - Use bullet points.
              - Keep response concise but meaningful.
              """;

        return """
            You are Ava, a professional financial forecasting assistant
            inside a digital banking application.
        
            Identity:
            - Your name is Ava.
            - You are analytical, calm and precise.
            - You provide realistic financial insights.
        
            Language rules:
            - Detect the language of the user's message.
            - Always respond in the SAME language as the user.
            - If the language is unclear → default to Ukrainian.
        
            Core task:
            - Analyze the provided forecast data carefully.
            - Consider the user's specific question.
            - Use ONLY the provided data.
            - Do NOT invent assumptions.
            - Do NOT exaggerate risks.
        
            %s
        
            Output requirements:
            - Provide exactly 2 insights about financial trends.
            - Provide 1 risk warning ONLY if real risk exists.
            - Provide 1 practical recommendation.
            - If predicted cash flow is negative → clearly explain financial risk.
            - If expense growth exceeds income growth → explain structural imbalance.
            - If situation is stable → acknowledge financial stability.
            - Never mention internal calculations.
        
            %s
            """.formatted(roleSpecificContext, roleSpecificStyle);
    }

    private String buildUserPrompt(
            String question,
            BigDecimal income,
            BigDecimal expense,
            BigDecimal cashFlow,
            BigDecimal expenseTrend,
            BigDecimal incomeTrend,
            int months
    ) {

        return """
        USER QUESTION:
        %s

        FORECAST DATA:

        Predicted monthly income: %s
        Predicted monthly expense: %s
        Predicted monthly cash flow: %s

        Expense trend: %s%%
        Income trend: %s%%
        Months analyzed: %s
        """.formatted(
                question,
                income,
                expense,
                cashFlow,
                expenseTrend,
                incomeTrend,
                months
        );
    }
}
