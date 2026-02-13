package com.lumina_bank.aiassistantservice.domain.intent.impl.payment;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.payment.PaymentTemplateResponse;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.exception.ExternalServiceException;
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
    public List<RequiredParam> requiredParams() {
        return List.of(
                new RequiredParam("templateId", ParamType.NUMBER, List.of())
        );
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params) {
        try {
            List<PaymentTemplateResponse> templates =
                    paymentGateway.getPaymentTemplates();

            if (templates.isEmpty()) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("У вас немає шаблонів.")
                );
            }

            // якщо один шаблон
            if (templates.size() == 1 && !params.containsKey("templateId")) {
                params.put("templateId", templates.getFirst().id());
            }

            // якщо багато і не вказано
            if (templates.size() > 1 && !params.containsKey("templateId")) {
                return AssistantExecutionResult.askParam(
                        intent(),
                        new RequiredParam(
                                "templateId",
                                ParamType.NUMBER,
                                templates.stream()
                                        .map(t -> t.id() + " | " + t.name() + " | " + t.amount())
                                        .toList()
                        )
                );
            }

//            Long templateId =
//                    Long.valueOf(params.get("templateId").toString());
//
//            PaymentTemplateResponse template =
//                    templates.stream()
//                            .filter(t -> t.id().equals(templateId))
//                            .findFirst()
//                            .orElseThrow();

            return AssistantExecutionResult.success(
                    intent(),
                    new EmptyData()
            );

        } catch (NumberFormatException e) {

            return AssistantExecutionResult.needClarification(
                    intent(),
                    new ClarificationData("Некоректний номер шаблону.")
            );

        } catch (ExternalServiceException e) {

            return AssistantExecutionResult.error(
                    intent(),
                    "Не вдалося отримати шаблони"
            );
        }
    }

    @Override
    public AssistantExecutionResult perform(Map<String, Object> params) {
        try {
            Long templateId =
                    Long.valueOf(params.get("templateId").toString());

            paymentGateway.makePaymentTemplate(templateId);

            return AssistantExecutionResult.success(
                    intent(),
                    new ClarificationData("Платіж виконано успішно.")
            );

        } catch (ExternalServiceException e) {

            return AssistantExecutionResult.error(
                    intent(),
                    "Не вдалося виконати платіж"
            );
        }
    }
}
