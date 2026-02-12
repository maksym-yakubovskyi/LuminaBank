package com.lumina_bank.aiassistantservice.domain.intent.impl;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.AccountCreateDto;
import com.lumina_bank.aiassistantservice.domain.result.data.AccountCreatedData;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.result.data.EmptyData;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.service.client.AccountClientService;
import com.lumina_bank.aiassistantservice.util.ParseEnumUtil;
import com.lumina_bank.common.enums.account.AccountType;
import com.lumina_bank.common.enums.payment.Currency;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateAccountIntent implements IntentDefinition {
    private final AccountClientService accountClient;

    @Override
    public Intent intent() {
        return Intent.CREATE_ACCOUNT;
    }

    @Override
    public List<RequiredParam> requiredParams() {
        return List.of(
                new RequiredParam(
                        "currency",
                        ParamType.ENUM,
                        ParseEnumUtil.enumValues(Currency.class)
                ),
                new RequiredParam(
                        "type",
                        ParamType.ENUM,
                        ParseEnumUtil.enumValues(AccountType.class)
                )
        );
    }

    @Override
    public boolean requiresFinalConfirmation() {
        return true;
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params) {

        if (!params.containsKey("currency")) {
            return AssistantExecutionResult.askParam(
                    intent(),
                    requiredParams().getFirst()
            );
        }

        if (!params.containsKey("type")) {
            return AssistantExecutionResult.askParam(
                    intent(),
                    requiredParams().get(1)
            );
        }

        return AssistantExecutionResult.success(intent(), new EmptyData());
    }

    @Override
    public AssistantExecutionResult perform(Map<String, Object> params) {

        Currency currency;
        AccountType type;

        try {
            currency = ParseEnumUtil.parseEnum(
                    Currency.class,
                    params.get("currency")
            );
            type = ParseEnumUtil.parseEnum(
                    AccountType.class,
                    params.get("type")
            );
        } catch (IllegalArgumentException e) {
            return AssistantExecutionResult.needClarification(
                    intent(),
                    new ClarificationData(
                            "Будь ласка, оберіть валюту та тип рахунку зі списку."
                    )
            );
        }

        try {
            var response = accountClient.createAccount(
                    new AccountCreateDto(currency, type)
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                return AssistantExecutionResult.error(
                        intent(),
                        "Не вдалося створити рахунок"
                );
            }

            return AssistantExecutionResult.success(
                    intent(),
                    new AccountCreatedData(response.getBody())
            );

        } catch (FeignException e) {
            log.warn("Account creation failed", e);
            return AssistantExecutionResult.error(
                    intent(),
                    "Помилка при створенні рахунку"
            );
        }
    }
}
