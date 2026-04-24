package com.lumina_bank.aiassistantservice.domain.assistant.intent.impl.analytics;

import com.lumina_bank.aiassistantservice.domain.assistant.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.assistant.RequiredParam;
import com.lumina_bank.aiassistantservice.infrastructure.external.analytics.dto.AnalyticsTopRecipientResponse;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.assistant.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.analytics.TopRecipientsAnalyticsData;
import com.lumina_bank.aiassistantservice.infrastructure.external.analytics.FeignAnalyticsGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                    new TopRecipientsAnalyticsData(recipients),
                    List.of(
                            Intent.ANALYTICS_MONTHLY,
                            Intent.ANALYTICS_BY_CATEGORY
                    )
            );

        } catch (ServiceCallException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage()
            );
        }
    }
}
