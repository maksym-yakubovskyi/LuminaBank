package com.lumina_bank.aiassistantservice.domain.intent.impl.payment;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.AccountResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.payment.TransactionHistoryItemDto;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.exception.ExternalServiceException;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.result.data.EmptyData;
import com.lumina_bank.aiassistantservice.domain.result.data.payment.PaymentHistoryData;
import com.lumina_bank.aiassistantservice.service.client.account.FeignAccountGateway;
import com.lumina_bank.aiassistantservice.service.client.payment.FeignPaymentGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentHistoryIntent implements IntentDefinition {
    private final FeignPaymentGateway paymentGateway;
    private final FeignAccountGateway accountGateway;

    @Override
    public Intent intent() {
        return Intent.PAYMENT_HISTORY;
    }

    @Override
    public List<RequiredParam> requiredParams() {
        return List.of(
                new RequiredParam(
                        "mode",
                        ParamType.ENUM,
                        List.of("ALL", "LIMITED")
                ),
                new RequiredParam(
                        "accountId",
                        ParamType.NUMBER,
                        List.of()
                ),
                new RequiredParam(
                        "limit",
                        ParamType.NUMBER,
                        List.of()
                )
        );
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params) {

        try {
            List<AccountResponse> accounts = accountGateway.getUserAccounts();

            if (accounts.isEmpty()) {
                return AssistantExecutionResult.needConfirmation(
                        intent(),
                        "У вас ще немає рахунків. Хочете створити рахунок?",
                        Intent.CREATE_ACCOUNT
                );
            }

            if (!params.containsKey("accountId")) {
                if (accounts.size() == 1) {
                    params.put("accountId", accounts.getFirst().id());
                } else {
                    return AssistantExecutionResult.askParam(
                            intent(),
                            new RequiredParam(
                                    "accountId",
                                    ParamType.NUMBER,
                                    accounts.stream()
                                            .map(a -> a.id() + " | " + a.currency() + " | " + a.iban())
                                            .toList()
                            )
                    );
                }
            }

            if (!params.containsKey("mode")) {
                return AssistantExecutionResult.askParam(
                        intent(),
                        new RequiredParam(
                                "mode",
                                ParamType.ENUM,
                                List.of("ALL", "LIMITED")
                        )
                );
            }

            String mode = params.get("mode").toString();

            if ("LIMITED".equalsIgnoreCase(mode)
                    && !params.containsKey("limit")) {

                return AssistantExecutionResult.askParam(
                        intent(),
                        new RequiredParam(
                                "limit",
                                ParamType.NUMBER,
                                List.of("наприклад: 5, 10, 20")
                        )
                );
            }

            Integer limit = null;

            if (params.containsKey("limit")) {
                try {
                    limit = Integer.valueOf(params.get("limit").toString());

                    if (limit <= 0) throw new IllegalArgumentException();

                } catch (Exception e) {
                    return AssistantExecutionResult.needClarification(
                            intent(),
                            new ClarificationData("Ліміт має бути додатним числом.")
                    );
                }
            }

            Long accountId = Long.valueOf(params.get("accountId").toString());

            boolean all = "ALL".equalsIgnoreCase(mode);

            List<TransactionHistoryItemDto> history =
                    paymentGateway.getHistory(limit, accountId, all);

            if (history.isEmpty()) {
                return AssistantExecutionResult.success(
                        intent(),
                        new EmptyData()
                );
            }

            return AssistantExecutionResult.success(
                    intent(),
                    new PaymentHistoryData(history)
            );

        } catch (ExternalServiceException e) {

            return AssistantExecutionResult.error(
                    intent(),
                    "Не вдалося отримати історію операцій"
            );
        }
    }
}
