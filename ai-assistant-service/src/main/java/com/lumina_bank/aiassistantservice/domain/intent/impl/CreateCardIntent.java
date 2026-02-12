package com.lumina_bank.aiassistantservice.domain.intent.impl;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.AccountResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.CardCreateDto;
import com.lumina_bank.aiassistantservice.domain.result.data.CardCreatedData;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.result.data.EmptyData;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.service.client.AccountClientService;
import com.lumina_bank.aiassistantservice.util.ParseEnumUtil;
import com.lumina_bank.common.enums.account.CardNetwork;
import com.lumina_bank.common.enums.account.CardType;
import feign.FeignException;
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
    private final AccountClientService accountClient;

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
        List<AccountResponse> accounts;
        try {
            var response = accountClient.getUserAccounts();
            accounts = response.getBody();
        } catch (FeignException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    "Не вдалося отримати рахунки"
            );
        }

        if (accounts == null || accounts.isEmpty()) {
            return AssistantExecutionResult.needConfirmation(
                    intent(),
                    "У вас ще немає рахунків. Щоб створити картку, потрібно спочатку створити рахунок. Продовжити?",
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

        BigDecimal limit;
        try {
            limit = new BigDecimal(params.get("limit").toString());
            if (limit.signum() < 0) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            return AssistantExecutionResult.needClarification(
                    intent(),
                    new ClarificationData("Ліміт має бути невідʼємним числом.")
            );
        }

        return AssistantExecutionResult.success(intent(), new EmptyData());
    }

    @Override
    public AssistantExecutionResult perform(Map<String, Object> params) {
        try {
            Long accountId = Long.valueOf(params.get("accountId").toString());

            BigDecimal limit = new BigDecimal(params.get("limit").toString());

            CardCreateDto dto = new CardCreateDto(
                    params.get("cardType").toString(),
                    params.get("cardNetwork").toString(),
                    limit
            );

            var response = accountClient.createCard(accountId, dto);

            if (!response.getStatusCode().is2xxSuccessful()) {
                return AssistantExecutionResult.error(intent(), "Не вдалося створити картку");
            }

            return AssistantExecutionResult.success(
                    intent(),
                    new CardCreatedData(response.getBody())
            );

        } catch (FeignException e) {
            log.warn("Card creation failed", e);
            return AssistantExecutionResult.error(intent(), "Помилка при створенні картки");
        }
    }
}
