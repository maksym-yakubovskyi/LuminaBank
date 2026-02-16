package com.lumina_bank.aiassistantservice.domain.intent.impl.analytics;

import com.lumina_bank.aiassistantservice.ai.AiModelService;
import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.data.analytics.ForecastAnalyticsData;
import com.lumina_bank.aiassistantservice.service.client.analytics.FeignAnalyticsGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
    private static final String FORECAST_PROMPT_SYSTEM = """
            You are a financial forecasting assistant.
            
            Analyze the financial forecast data and provide:
            
            - 2 insights about future financial trend
            - 1 warning if overspending risk exists
            - 1 practical recommendation
            - Keep it concise and realistic
            - Do NOT invent numbers
            - Use only the provided data
            
            If predicted cash flow is negative, clearly state financial risk.
            If expense growth exceeds income growth, highlight imbalance.
            
            Return bullet points.
            """;

    private static final String FORECAST_PROMPT_USER = """
            Forecast data:
            
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
    public AssistantExecutionResult execute(Map<String, Object> params) {
        try {

            var forecast = analyticsGateway.getForecast();

            String prompt = FORECAST_PROMPT_USER.formatted(
                    forecast.predictedIncome(),
                    forecast.predictedExpense(),
                    forecast.predictedCashFlow(),
                    forecast.expenseTrendPercent(),
                    forecast.incomeTrendPercent(),
                    forecast.monthsAnalyzed()
            );

            String aiResponse = aiModelService.generateText(
                    FORECAST_PROMPT_SYSTEM,prompt,"forecast");

            return AssistantExecutionResult.success(
                    intent(),
                    new ForecastAnalyticsData(aiResponse,prompt)
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
}
