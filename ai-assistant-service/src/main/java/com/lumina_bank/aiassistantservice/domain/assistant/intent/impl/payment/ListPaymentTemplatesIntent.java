package com.lumina_bank.aiassistantservice.domain.assistant.intent.impl.payment;

import com.lumina_bank.aiassistantservice.domain.assistant.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.assistant.RequiredParam;
import com.lumina_bank.aiassistantservice.infrastructure.external.payment.dto.PaymentTemplateResponse;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.assistant.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.payment.PaymentTemplatesData;
import com.lumina_bank.aiassistantservice.infrastructure.external.payment.FeignPaymentGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ListPaymentTemplatesIntent implements IntentDefinition {
    private final FeignPaymentGateway paymentGateway;

    @Override
    public Intent intent() {
        return Intent.LIST_PAYMENT_TEMPLATES;
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
            List<PaymentTemplateResponse> templates =
                    paymentGateway.getPaymentTemplates();

            if (templates.isEmpty()) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("NO_PAYMENT_TEMPLATES")
                );
            }

            return AssistantExecutionResult.success(
                    intent(),
                    new PaymentTemplatesData(templates)
            );

        } catch (ServiceCallException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage()
            );
        }
    }
}
