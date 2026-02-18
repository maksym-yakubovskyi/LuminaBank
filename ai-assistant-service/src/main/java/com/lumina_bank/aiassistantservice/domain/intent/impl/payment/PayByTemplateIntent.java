package com.lumina_bank.aiassistantservice.domain.intent.impl.payment;

import com.lumina_bank.aiassistantservice.domain.dto.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.payment.PaymentTemplateResponse;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.result.data.EmptyData;
import com.lumina_bank.aiassistantservice.service.client.payment.FeignPaymentGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PayByTemplateIntent implements IntentDefinition {

    private final FeignPaymentGateway paymentGateway;

    @Override
    public Intent intent() {
        return Intent.PAYMENT_BY_TEMPLATE;
    }

    @Override
    public boolean requiresFinalConfirmation() {
        return true;
    }

    @Override
    public List<RequiredParam> requiredParams(AssistantContext context) {
        return List.of(
                new RequiredParam(
                        "templateId",
                        ParamType.NUMBER,
                        List.of(),
                        "ID of the payment template")
        );
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
                        new ClarificationData("NO_TEMPLATES")
                );
            }

            if (!params.containsKey("templateId")) {

                if (templates.size() == 1) {
                    params.put("templateId", templates.getFirst().id());
                } else {
                    return AssistantExecutionResult.askParam(
                            intent(),
                            new RequiredParam(
                                    "templateId",
                                    ParamType.NUMBER,
                                    templates.stream()
                                            .map(t -> t.id() + " | " + t.name() + " | " + t.amount())
                                            .toList(),
                                    "Payment template list to select"
                            )
                    );
                }
            }
            long templateId;

            try {
                templateId = Long.parseLong(params.get("templateId").toString());
            } catch (NumberFormatException e) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("INVALID_TEMPLATE_ID_FORMAT")
                );
            }

            boolean exists = templates.stream()
                    .anyMatch(t -> t.id().equals(templateId));

            if (!exists) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("TEMPLATE_NOT_FOUND")
                );
            }

            return AssistantExecutionResult.success(
                    intent(),
                    new EmptyData()
            );

        } catch (ServiceCallException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage()
            );
        }
    }

    @Override
    public AssistantExecutionResult perform(Map<String, Object> params,
                                            AssistantContext context) {
        try {
            Long templateId = Long.valueOf(
                    params.get("templateId").toString()
            );

            paymentGateway.makePaymentTemplate(templateId);

            return AssistantExecutionResult.success(
                    intent(),
                    new EmptyData()
            );

        } catch (ServiceCallException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage()
            );
        }
    }
}
