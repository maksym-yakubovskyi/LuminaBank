package com.lumina_bank.aiassistantservice.domain.assistant.intent.impl.payment;

import com.lumina_bank.aiassistantservice.domain.assistant.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.assistant.RequiredParam;
import com.lumina_bank.aiassistantservice.infrastructure.external.account.dto.CardResponse;
import com.lumina_bank.aiassistantservice.infrastructure.external.payment.dto.PaymentRequest;
import com.lumina_bank.aiassistantservice.infrastructure.external.payment.dto.PaymentResponse;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.assistant.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.ConfirmationData;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.EmptyData;
import com.lumina_bank.aiassistantservice.infrastructure.external.account.FeignAccountGateway;
import com.lumina_bank.aiassistantservice.infrastructure.external.payment.FeignPaymentGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    public List<RequiredParam> requiredParams(AssistantContext context) {
        return List.of(
                new RequiredParam(
                        "fromCardNumber",
                        ParamType.STRING,
                        List.of(),
                        "Sender card number. Must be one of the user's own cards."),
                new RequiredParam(
                        "toCardNumber",
                        ParamType.STRING,
                        List.of(),
                        "Recipient card number. Any valid card number."),
                new RequiredParam(
                        "amount",
                        ParamType.NUMBER,
                        List.of(),
                        "Payment amount. Must be a positive number."),
                new RequiredParam(
                        "description",
                        ParamType.STRING,
                        List.of(),
                        "Optional payment description.")
        );
    }

    @Override
    public AssistantExecutionResult execute(
            Map<String, Object> params,
            UUID conversationId,
            AssistantContext context
    ) {
        try {
            List<CardResponse> myCards = accountGateway.getMyCards();

            if (myCards.isEmpty()) {
                return AssistantExecutionResult.confirmNavigation(
                        intent(),
                        new ConfirmationData(
                                "NO_CARDS",
                                Map.of("nextIntent",Intent.CREATE_CARD)),
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
                                            .toList(),
                                    "User's card number list to select"
                            )
                    );
                }
            }

            if (!params.containsKey("toCardNumber")) {
                return AssistantExecutionResult.askParam(
                        intent(),
                        requiredParams(context).get(1)
                );
            }

            if (!params.containsKey("amount")) {
                return AssistantExecutionResult.askParam(
                        intent(),
                        requiredParams(context).get(2)
                );
            }

            BigDecimal amount;

            try {
                amount = new BigDecimal(
                        params.get("amount").toString()
                );
            } catch (NumberFormatException e) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("INVALID_AMOUNT_FORMAT")
                );
            }

            if (amount.signum() <= 0) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("AMOUNT_MUST_BE_POSITIVE")
                );
            }

            if (!params.containsKey("description")) {
                return AssistantExecutionResult.askParam(
                        intent(),
                        requiredParams(context).get(3)
                );
            }

            return AssistantExecutionResult.success(intent(), new EmptyData());

        }catch (ServiceCallException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage()
            );
        }
    }

    @Override
    public AssistantExecutionResult perform(Map<String, Object> params,AssistantContext context) {
        try {
            String fromCardNumber = params.get("fromCardNumber").toString().replaceAll("\\s+", "");
            String toCardNumber = params.get("toCardNumber").toString().replaceAll("\\s+", "");

            PaymentResponse payment = paymentGateway.makePayment(
                    new PaymentRequest(
                            fromCardNumber,
                            toCardNumber,
                            new BigDecimal(params.get("amount").toString()),
                            (String) params.get("description")
                    )
            );

            return AssistantExecutionResult.success(
                    intent(),
                    new EmptyData(),
                    List.of(
                            Intent.PAYMENT_HISTORY,
                            Intent.ANALYTICS_MONTHLY,
                            Intent.LIST_PAYMENT_TEMPLATES
                    )
            );

        }catch (NumberFormatException e) {
            return AssistantExecutionResult.needClarification(
                    intent(),
                    new ClarificationData("INVALID_PARAMS")
            );
        } catch (ServiceCallException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage()
            );
        }
    }
}
