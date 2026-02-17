package com.lumina_bank.aiassistantservice.domain.intent.impl.analytics;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.analytics.AnalyticsCategoryResponse;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.result.data.analytics.CategoryAnalyticsData;
import com.lumina_bank.aiassistantservice.service.client.analytics.FeignAnalyticsGateway;
import com.lumina_bank.aiassistantservice.util.ParseDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryAnalyticsIntent implements IntentDefinition {

    private final FeignAnalyticsGateway analyticsGateway;

    @Override
    public Intent intent() {
        return Intent.ANALYTICS_BY_CATEGORY;
    }

    @Override
    public List<RequiredParam> requiredParams() {
        return List.of(
                new RequiredParam(
                        "yearMonth",
                        ParamType.DATE,
                        List.of(),
                        "Month for analytics")
        );
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params) {
        try {
            YearMonth yearMonth;

            if (!params.containsKey("yearMonth")) {
                yearMonth = YearMonth.now();
            } else {
                Optional<YearMonth> parsed =
                        ParseDateUtil.parseYearMonth(
                                params.get("yearMonth")
                        );

                if (parsed.isEmpty()) {
                    return AssistantExecutionResult.needClarification(
                            intent(),
                            new ClarificationData("INVALID_YEAR_MONTH_FORMAT")
                    );
                }

                yearMonth = parsed.get();
            }

            List<AnalyticsCategoryResponse> categories =
                    analyticsGateway.getCategoriesAnalytics(yearMonth);

            if (categories.isEmpty()) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("NO_CATEGORY_ANALYTICS_DATA")
                );
            }

            return AssistantExecutionResult.success(
                    intent(),
                    new CategoryAnalyticsData(categories)
            );
        } catch (ServiceCallException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage()
            );
        }
    }
}

