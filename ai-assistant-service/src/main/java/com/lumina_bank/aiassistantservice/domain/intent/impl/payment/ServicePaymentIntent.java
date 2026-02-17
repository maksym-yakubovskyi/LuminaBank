package com.lumina_bank.aiassistantservice.domain.intent.impl.payment;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.CardResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.payment.PaymentResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.payment.ServicePaymentRequest;
import com.lumina_bank.aiassistantservice.domain.dto.client.user.BusinessUserProviderResponse;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.result.data.ConfirmationData;
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
                new RequiredParam("fromCardNumber", ParamType.STRING, List.of(),"Sender card number. Must be one of the user's own cards."),
                new RequiredParam("providerId", ParamType.NUMBER, List.of(),"ID of the service provider."),
                new RequiredParam("amount", ParamType.NUMBER, List.of(),"Payment amount. Must be a positive number."),
                new RequiredParam("payerReference", ParamType.STRING, List.of(),
                        "Identifier required by the service provider to identify the payer. " +
                                "Can be phone number, contract number, personal account number, email, or any reference provided by user."
                ),
                new RequiredParam("description", ParamType.STRING, List.of(),"Optional payment description.")
        );
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params) {
        try {
            List<CardResponse> cards = accountGateway.getMyCards();

            if (cards.isEmpty()) {
                return AssistantExecutionResult.confirmNavigation(
                        intent(),
                        new ConfirmationData(
                                "NO_CARDS",
                                Map.of("nextIntent",Intent.CREATE_CARD)),
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
                                            .toList(),
                                    "User's card number list to select"
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
                                        .toList(),
                                "Provider list to select"
                        )
                );
            }

            if (!params.containsKey("amount")) {
                return AssistantExecutionResult.askParam(
                        intent(),
                        requiredParams().get(2)
                );
            }

            BigDecimal amount = new BigDecimal(params.get("amount").toString());

            if (amount.signum() <= 0){
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("AMOUT_NEGATIVE")
                );
            }

            if (!params.containsKey("payerReference")) {
                return AssistantExecutionResult.askParam(intent(), requiredParams().get(3));
            }

            if (!params.containsKey("description")) {
                return AssistantExecutionResult.askParam(
                        intent(),
                        requiredParams().get(4)
                );
            }

            return AssistantExecutionResult.success(intent(), new EmptyData());

        }catch (NumberFormatException e) {
            return AssistantExecutionResult.needClarification(
                    intent(),
                    new ClarificationData("INVALID_AMOUNT_FORMAT")
            );
        } catch (ServiceCallException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage()
            );
        }
    }

    @Override
    public AssistantExecutionResult perform(Map<String, Object> params) {
        try {
            Long providerId = Long.valueOf(params.get("providerId").toString());

            List<BusinessUserProviderResponse> providers = userGateway.getProviders();

            BusinessUserProviderResponse selected = providers.stream()
                    .filter(p -> p.id().equals(providerId))
                    .findFirst()
                    .orElse(null);

            if (selected == null) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("PROVIDER_NOT_FOUND")
                );
            }

            PaymentResponse payment =
                    paymentGateway.makePaymentService(
                            new ServicePaymentRequest(
                                    params.get("fromCardNumber").toString(),
                                    providerId,
                                    selected.category(),
                                    new BigDecimal(params.get("amount").toString()),
                                    params.get("payerReference").toString(),
                                    (String) params.get("description")
                            )
                    );

            return AssistantExecutionResult.success(
                    intent(),
                    new EmptyData()
            );

        } catch (NumberFormatException e) {
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
