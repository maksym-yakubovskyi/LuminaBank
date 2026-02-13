package com.lumina_bank.aiassistantservice.domain.intent.impl.account;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.AccountResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.CardCreateDto;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.CardResponse;
import com.lumina_bank.aiassistantservice.domain.exception.ExternalServiceException;
import com.lumina_bank.aiassistantservice.domain.result.data.account.CardCreatedData;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.result.data.EmptyData;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.service.client.account.FeignAccountGateway;
import com.lumina_bank.aiassistantservice.util.ParseEnumUtil;
import com.lumina_bank.common.enums.account.CardNetwork;
import com.lumina_bank.common.enums.account.CardType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateCardIntent implements IntentDefinition{
    private final FeignAccountGateway accountGateway;

    @Override
    public Intent intent() {
        return Intent.CREATE_CARD;
    }

    @Override
    public List<RequiredParam> requiredParams() {
        return List.of(
                new RequiredParam(
                        "accountId",
                        ParamType.NUMBER,
                        List.of()
                ),
                new RequiredParam(
                        "cardType",
                        ParamType.ENUM,
                        ParseEnumUtil.enumValues(CardType.class)
                ),
                new RequiredParam(
                        "cardNetwork",
                        ParamType.ENUM,
                        ParseEnumUtil.enumValues(CardNetwork.class)
                ),
                new RequiredParam(
                        "limit",
                        ParamType.NUMBER,
                        List.of("мін: 0, не від'ємне")
                )
        );
    }

    @Override
    public boolean requiresFinalConfirmation() {
        return true;
    }

    @Override
    public AssistantExecutionResult execute(
            Map<String, Object> params
    ) {
        try {
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

            if (!params.containsKey("cardType")) {
                return AssistantExecutionResult.askParam(
                        intent(),
                        requiredParams().get(1)
                );
            }

            if (!params.containsKey("cardNetwork")) {
                return AssistantExecutionResult.askParam(
                        intent(),
                        requiredParams().get(2)
                );
            }

            if (!params.containsKey("limit")) {
                return AssistantExecutionResult.askParam(
                        intent(),
                        requiredParams().get(3)
                );
            }

            BigDecimal limit = new BigDecimal(params.get("limit").toString());

            if (limit.signum() < 0)
                throw new IllegalArgumentException();

            return AssistantExecutionResult.success(intent(), new EmptyData());

        } catch (IllegalArgumentException e) {
            return AssistantExecutionResult.needClarification(
                    intent(),
                    new ClarificationData("Ліміт має бути невідʼємним числом.")
            );

        } catch (ExternalServiceException e) {
            return AssistantExecutionResult.error(intent(),
                    "Не вдалося отримати рахунки");
        }
    }

    @Override
    public AssistantExecutionResult perform(Map<String, Object> params) {
        try {
            Long accountId = Long.valueOf(params.get("accountId").toString());
            BigDecimal limit = new BigDecimal(params.get("limit").toString());

            CardResponse created = accountGateway.createCard(accountId,
                    new CardCreateDto(
                            params.get("cardType").toString(),
                            params.get("cardNetwork").toString(),
                            limit));

            return AssistantExecutionResult.success(
                    intent(),
                    new CardCreatedData(created)
            );

        } catch (ExternalServiceException e) {
            return AssistantExecutionResult.error(intent(),
                    "Не вдалося створити картку");
        }
    }
}
