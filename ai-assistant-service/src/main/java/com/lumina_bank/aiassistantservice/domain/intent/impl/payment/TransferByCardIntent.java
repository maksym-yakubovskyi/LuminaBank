package com.lumina_bank.aiassistantservice.domain.intent.impl.payment;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.CardResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.payment.PaymentRequest;
import com.lumina_bank.aiassistantservice.domain.dto.client.payment.PaymentResponse;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.exception.ExternalServiceException;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.result.data.EmptyData;
import com.lumina_bank.aiassistantservice.service.client.account.FeignAccountGateway;
import com.lumina_bank.aiassistantservice.service.client.payment.FeignPaymentGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransferByCardIntent implements IntentDefinition {
    private final FeignPaymentGateway paymentGateway;
    private final FeignAccountGateway accountGateway;

    @Override
    public Intent intent() {
        return Intent.TRANSFER_BY_CARD;
    }

    @Override
    public boolean requiresFinalConfirmation() {
        return true;
    }

    @Override
    public List<RequiredParam> requiredParams() {
        return List.of(
                new RequiredParam("fromCardNumber", ParamType.STRING, List.of()),
                new RequiredParam("toCardNumber", ParamType.STRING, List.of()),
                new RequiredParam("amount", ParamType.NUMBER, List.of()),
                new RequiredParam("description", ParamType.STRING, List.of()));
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params) {

        try {
            List<CardResponse> myCards = accountGateway.getMyCards();

            if (myCards.isEmpty()) {
                return AssistantExecutionResult.needConfirmation(
                        intent(),
                        "У вас ще немає карток. Хочете створити картку?",
                        Intent.CREATE_CARD
                );
            }

            if (!params.containsKey("fromCardNumber")) {

                if (myCards.size() == 1) {
                    params.put("fromCardNumber", myCards.getFirst().cardNumber());
                } else {
                    return AssistantExecutionResult.askParam(
                            intent(),
                            new RequiredParam(
                                    "fromCardNumber",
                                    ParamType.STRING,
                                    myCards.stream()
                                            .map(CardResponse::cardNumber)
                                            .toList()
                            )
                    );
                }
            }

            if (!params.containsKey("toCardNumber")) {
                return AssistantExecutionResult.askParam(
                        intent(),
                        requiredParams().get(1)
                );
            }

            if (!params.containsKey("amount")) {
                return AssistantExecutionResult.askParam(
                        intent(),
                        requiredParams().get(2)
                );
            }

            BigDecimal amount = new BigDecimal(params.get("amount").toString());
            if (amount.signum() <= 0)
                throw new IllegalArgumentException();

            return AssistantExecutionResult.success(intent(), new EmptyData());

        } catch (IllegalArgumentException e) {
            return AssistantExecutionResult.needClarification(
                    intent(),
                    new ClarificationData("Сума має бути більше 0.")
            );

        } catch (ExternalServiceException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    "Не вдалося отримати список карток"
            );
        }
    }

    @Override
    public AssistantExecutionResult perform(Map<String, Object> params) {
        try {
            PaymentResponse payment = paymentGateway.makePayment(
                    new PaymentRequest(
                            params.get("fromCardNumber").toString(),
                            params.get("toCardNumber").toString(),
                            new BigDecimal(params.get("amount").toString()),
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
                    "Не вдалося виконати переказ"
            );
        }
    }
}
