package com.lumina_bank.aiassistantservice.domain.intent.impl.account;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.AccountCreateDto;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.AccountResponse;
import com.lumina_bank.aiassistantservice.domain.exception.ExternalServiceException;
import com.lumina_bank.aiassistantservice.domain.result.data.account.AccountCreatedData;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.result.data.EmptyData;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.service.client.account.FeignAccountGateway;
import com.lumina_bank.aiassistantservice.util.ParseEnumUtil;
import com.lumina_bank.common.enums.account.AccountType;
import com.lumina_bank.common.enums.payment.Currency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateAccountIntent implements IntentDefinition {
    private final FeignAccountGateway accountGateway;

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
        try {
            Currency currency = ParseEnumUtil.parseEnum(
                    Currency.class,
                    params.get("currency")
            );
            AccountType type = ParseEnumUtil.parseEnum(
                    AccountType.class,
                    params.get("type")
            );

            AccountResponse created =
                    accountGateway.createAccount(new AccountCreateDto(currency, type));

            return AssistantExecutionResult.success(
                    intent(),
                    new AccountCreatedData(created)
            );

        } catch (IllegalArgumentException e) {
            return AssistantExecutionResult.needClarification(
                    intent(),
                    new ClarificationData("Будь ласка, оберіть валюту та тип рахунку зі списку.")
            );

        } catch (ExternalServiceException e) {
            return AssistantExecutionResult.error(intent(),
                    "Не вдалося створити рахунок");
        }
    }
}
