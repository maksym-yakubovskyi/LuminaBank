package com.lumina_bank.aiassistantservice.domain.intent.impl.account;

import com.lumina_bank.aiassistantservice.domain.dto.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.AccountResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.CardResponse;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.data.ConfirmationData;
import com.lumina_bank.aiassistantservice.domain.result.data.account.CardsListData;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.service.client.account.FeignAccountGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ListAccountCardsIntent implements IntentDefinition {
    private final FeignAccountGateway accountGateway;

    @Override
    public Intent intent() {
        return Intent.LIST_ACCOUNT_CARDS;
    }

    @Override
    public List<RequiredParam> requiredParams(AssistantContext context) {
        return List.of(
                new RequiredParam(
                        "accountId",
                        ParamType.NUMBER,
                        List.of(),
                        "ID of the account")
        );
    }

    @Override
    public AssistantExecutionResult execute(
            Map<String, Object> params,
            UUID conversationId,
            AssistantContext context
    ) {
        try{
            List<AccountResponse> accounts = accountGateway.getUserAccounts();

            if (accounts.isEmpty()) {
                return AssistantExecutionResult.confirmNavigation(
                        intent(),
                        new ConfirmationData(
                                "NO_ACCOUNTS",
                                Map.of("nextIntent",Intent.CREATE_ACCOUNT)),
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

            Long accountId;

            try {
                accountId = Long.valueOf(
                        params.get("accountId").toString()
                );
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

            List<CardResponse> cards = accountGateway.getCardsByAccountId(accountId);

            if (cards.isEmpty()) {
                return AssistantExecutionResult.confirmNavigation(
                        intent(),
                        new ConfirmationData(
                                "NO_CARDS",
                                Map.of("nextIntent",Intent.CREATE_CARD)),
                        Intent.CREATE_CARD
                );
            }

            return AssistantExecutionResult.success(
                    intent(),
                    new CardsListData(cards)
            );

        }catch (ServiceCallException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage()
            );
        }
    }
}
