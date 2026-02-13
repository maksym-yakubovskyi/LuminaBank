package com.lumina_bank.aiassistantservice.domain.intent.impl.account;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.AccountResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.CardResponse;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.exception.ExternalServiceException;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.data.account.CardsListData;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.service.client.account.FeignAccountGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
    public List<RequiredParam> requiredParams() {
        return List.of(
                new RequiredParam("accountId", ParamType.NUMBER, List.of())
        );
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params) {
        try{
            List<AccountResponse> accounts = accountGateway.getUserAccounts();

            if (accounts.isEmpty()) {
                return AssistantExecutionResult.needConfirmation(
                        intent(),
                        "У вас ще немає рахунків. Хочете створити рахунок?",
                        Intent.CREATE_ACCOUNT
                );
            }

            if (accounts.size() == 1 && !params.containsKey("accountId")) {
                params.put("accountId", accounts.getFirst().id());
            }

            if (accounts.size() > 1 && !params.containsKey("accountId")) {
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

            Long accountId = Long.valueOf(params.get("accountId").toString());

            List<CardResponse> cards = accountGateway.getCardsByAccountId(accountId);

            if (cards.isEmpty()) {
                return AssistantExecutionResult.needConfirmation(
                        intent(),
                        "На цьому рахунку ще немає карт. Хочете створити картку?",
                        Intent.CREATE_CARD
                );
            }

            return AssistantExecutionResult.success(
                    intent(),
                    new CardsListData(cards)
            );

        } catch (NumberFormatException e) {

            return AssistantExecutionResult.needClarification(
                    intent(),
                    new ClarificationData("Некоректний номер рахунку.")
            );

        } catch (ExternalServiceException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    "Не вдалося отримати картки цього рахунку"
            );
        }
    }
}
