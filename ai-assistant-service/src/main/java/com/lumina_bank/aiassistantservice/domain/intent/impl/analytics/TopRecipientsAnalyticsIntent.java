package com.lumina_bank.aiassistantservice.domain.intent.impl.analytics;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.analytics.AnalyticsTopRecipientResponse;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.result.data.analytics.TopRecipientsAnalyticsData;
import com.lumina_bank.aiassistantservice.service.client.analytics.FeignAnalyticsGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TopRecipientsAnalyticsIntent implements IntentDefinition {

    private final FeignAnalyticsGateway analyticsGateway;

    @Override
    public Intent intent() {
        return Intent.ANALYTICS_TOP_RECIPIENTS;
    }

    @Override
    public List<RequiredParam> requiredParams() {
        return List.of();
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params) {
        try {
            List<AnalyticsTopRecipientResponse> recipients =
                    analyticsGateway.getTopRecipients();

            if (recipients.isEmpty()) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("NO_TOP_RECIPIENTS_DATA")
                );
            }

            return AssistantExecutionResult.success(
                    intent(),
                    new TopRecipientsAnalyticsData(recipients)
            );

        } catch (ServiceCallException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage()
            );
        }
    }
}
