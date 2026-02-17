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
public class ForecastAnalyticsIntent implements IntentDefinition {
    private final FeignAnalyticsGateway analyticsGateway;
    private final AiModelService aiModelService;

    @Override
    public Intent intent() {
        return Intent.ANALYTICS_FORECAST;
    }

    private static final String SYSTEM_PROMPT = """
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
    - Do NOT invent any additional assumptions.
    - Do NOT exaggerate risks.
    - Do NOT provide unrealistic financial predictions.

    Output requirements:
    - Provide exactly 2 insights about financial trends.
    - Provide 1 risk warning ONLY if real risk exists.
    - Provide 1 practical recommendation.
    - If predicted cash flow is negative → clearly explain financial risk.
    - If expense growth exceeds income growth → explain structural imbalance.
    - If situation is stable → acknowledge financial stability.

    Style:
    - Professional but human.
    - Clear and structured.
    - Analytical, not emotional.
    - Avoid dramatic language.
    - Use bullet points.
    - Keep response concise but meaningful.

    Never mention internal calculations or system logic.
    """;

    private static final String USER_TEMPLATE = """
        USER QUESTION:
        %s

        FORECAST DATA:

        Predicted monthly income: %s
        Predicted monthly expense: %s
        Predicted monthly cash flow: %s

        Expense trend over period: %s%%
        Income trend over period: %s%%
        Months analyzed: %s
        """;

    @Override
    public List<RequiredParam> requiredParams() {
        return List.of();
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params, UUID conversationId) {
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

            String userPrompt  = USER_TEMPLATE.formatted(
                    userMessage,
                    forecast.predictedIncome(),
                    forecast.predictedExpense(),
                    forecast.predictedCashFlow(),
                    forecast.expenseTrendPercent(),
                    forecast.incomeTrendPercent(),
                    forecast.monthsAnalyzed()
            );

            String aiResponse = aiModelService.generateText(
                    SYSTEM_PROMPT,
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

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params) {
        return AssistantExecutionResult.error(
                intent(),
                "CONVERSATION_REQUIRED"
        );
    }
}
