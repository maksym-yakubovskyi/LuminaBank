package com.lumina_bank.aiassistantservice.domain.assistant.intent.impl.payment;

import com.lumina_bank.aiassistantservice.domain.assistant.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.assistant.RequiredParam;
import com.lumina_bank.aiassistantservice.infrastructure.external.account.dto.AccountResponse;
import com.lumina_bank.aiassistantservice.infrastructure.external.payment.dto.TransactionHistoryItemResponse;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.assistant.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.ConfirmationData;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.EmptyData;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.payment.PaymentHistoryData;
import com.lumina_bank.aiassistantservice.infrastructure.external.account.FeignAccountGateway;
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
public class PaymentHistoryIntent implements IntentDefinition {
    private final FeignPaymentGateway paymentGateway;
    private final FeignAccountGateway accountGateway;

    @Override
    public Intent intent() {
        return Intent.PAYMENT_HISTORY;
    }

    @Override
    public List<RequiredParam> requiredParams(AssistantContext context) {
        return List.of(
                new RequiredParam(
                        "accountId",
                        ParamType.NUMBER,
                        List.of(),
                        "ID of the account."),
                new RequiredParam(
                        "mode",
                        ParamType.ENUM,
                        List.of("ALL", "LIMITED"),
                        "History mode. 'ALL' means full history, 'LIMITED' means limited number of recent transactions."),
                new RequiredParam(
                        "limit",
                        ParamType.NUMBER,
                        List.of(),
                        "Number of recent transactions to return. Must be a positive number.")
        );
    }
    @Override
    public AssistantExecutionResult execute(
            Map<String, Object> params,
            UUID conversationId,
            AssistantContext context
    ) {
        try {
            List<AccountResponse> accounts = accountGateway.getUserAccounts();

            if (accounts.isEmpty()) {
                return AssistantExecutionResult.confirmNavigation(
                        intent(),
                        new ConfirmationData(
                                "NO_ACCOUNTS",
                                Map.of("nextIntent", Intent.CREATE_ACCOUNT)
                        ),
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
                                            .toList(),
                                    "Accounts list to select"
                            )
                    );
                }
            }

            long accountId;

            try {
                accountId = Long.parseLong(params.get("accountId").toString());
            } catch (NumberFormatException e) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("INVALID_ACCOUNT_ID")
                );
            }

            boolean exists = accounts.stream()
                    .anyMatch(a -> a.id().equals(accountId));

            if (!exists) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("ACCOUNT_NOT_FOUND")
                );
            }

            if (!params.containsKey("mode")) {
                return AssistantExecutionResult.askParam(
                        intent(),
                        requiredParams(context).get(1)
                );
            }

            String modeRaw = params.get("mode").toString().trim().toUpperCase();

            if (!modeRaw.equals("ALL") && !modeRaw.equals("LIMITED")) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("INVALID_MODE")
                );
            }

            boolean all = modeRaw.equals("ALL");
            Integer limit = null;

            if (!all) {

                if (!params.containsKey("limit")) {
                    return AssistantExecutionResult.askParam(
                            intent(),
                            requiredParams(context).get(2)
                    );
                }

                try {
                    limit = Integer.parseInt(
                            params.get("limit").toString()
                    );
                } catch (NumberFormatException e) {
                    return AssistantExecutionResult.needClarification(
                            intent(),
                            new ClarificationData("INVALID_LIMIT_FORMAT")
                    );
                }

                if (limit <= 0) {
                    return AssistantExecutionResult.needClarification(
                            intent(),
                            new ClarificationData("LIMIT_MUST_BE_POSITIVE")
                    );
                }
            }

            List<TransactionHistoryItemResponse> history =
                    paymentGateway.getHistory(limit, accountId, all);

            if (history.isEmpty()) {
                return AssistantExecutionResult.success(
                        intent(),
                        new EmptyData()
                );
            }

            return AssistantExecutionResult.success(
                    intent(),
                    new PaymentHistoryData(history),
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
