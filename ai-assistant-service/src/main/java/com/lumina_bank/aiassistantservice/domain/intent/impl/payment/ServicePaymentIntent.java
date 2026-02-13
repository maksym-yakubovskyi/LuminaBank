package com.lumina_bank.aiassistantservice.domain.intent.impl.payment;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.CardResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.payment.PaymentResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.payment.ServicePaymentRequest;
import com.lumina_bank.aiassistantservice.domain.dto.client.user.BusinessUserProviderResponse;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.exception.ExternalServiceException;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.data.EmptyData;
import com.lumina_bank.aiassistantservice.service.client.account.FeignAccountGateway;
import com.lumina_bank.aiassistantservice.service.client.payment.FeignPaymentGateway;
import com.lumina_bank.aiassistantservice.service.client.user.FeignUserGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServicePaymentIntent implements IntentDefinition {
    private final FeignPaymentGateway paymentGateway;
    private final FeignAccountGateway accountGateway;
    private final FeignUserGateway userGateway;

    @Override
    public Intent intent() {
        return Intent.PAYMENT_SERVICE_PROVIDER;
    }

    @Override
    public boolean requiresFinalConfirmation() {
        return true;
    }

    @Override
    public List<RequiredParam> requiredParams() {
        return List.of(
                new RequiredParam("fromCardNumber", ParamType.STRING, List.of()),
                new RequiredParam("providerId", ParamType.NUMBER, List.of()),
                new RequiredParam("category", ParamType.STRING, List.of()),
                new RequiredParam("amount", ParamType.NUMBER, List.of()),
                new RequiredParam("payerReference", ParamType.STRING, List.of()),
                new RequiredParam("description", ParamType.STRING, List.of())
        );
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params) {
        try {
            List<CardResponse> cards = accountGateway.getMyCards();

            if (cards.isEmpty()) {
                return AssistantExecutionResult.needConfirmation(
                        intent(),
                        "У вас ще немає карток. Хочете створити картку?",
                        Intent.CREATE_CARD
                );
            }

            if (!params.containsKey("fromCardNumber")) {
                if (cards.size() == 1) {
                    params.put("fromCardNumber", cards.getFirst().cardNumber());
                } else {
                    return AssistantExecutionResult.askParam(
                            intent(),
                            new RequiredParam(
                                    "fromCardNumber",
                                    ParamType.STRING,
                                    cards.stream()
                                            .map(CardResponse::cardNumber)
                                            .toList()
                            )
                    );
                }
            }

            if (!params.containsKey("providerId")) {

                List<BusinessUserProviderResponse> providers = userGateway.getProviders();

                return AssistantExecutionResult.askParam(
                        intent(),
                        new RequiredParam(
                                "providerId",
                                ParamType.NUMBER,
                                providers.stream()
                                        .map(p -> p.id() + " | " + p.companyName() + " | " + p.category())
                                        .toList()
                        )
                );
            }

            if (!params.containsKey("amount")) {
                return AssistantExecutionResult.askParam(
                        intent(),
                        requiredParams().get(3)
                );
            }

            return AssistantExecutionResult.success(intent(), new EmptyData());

        } catch (ExternalServiceException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    "Не вдалося отримати необхідні дані"
            );
        }
    }

    @Override
    public AssistantExecutionResult perform(Map<String, Object> params) {
        try {
            PaymentResponse payment =
                    paymentGateway.makePaymentService(
                            new ServicePaymentRequest(
                                    params.get("fromCardNumber").toString(),
                                    Long.valueOf(params.get("providerId").toString()),
                                    params.get("category").toString(),
                                    new BigDecimal(params.get("amount").toString()),
                                    params.get("payerReference").toString(),
                                    (String) params.get("description")
                            )
                    );

            return AssistantExecutionResult.success(
                    intent(),
                    new EmptyData()
            );

        } catch (ExternalServiceException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    "Не вдалося виконати оплату"
            );
        }
    }
}
