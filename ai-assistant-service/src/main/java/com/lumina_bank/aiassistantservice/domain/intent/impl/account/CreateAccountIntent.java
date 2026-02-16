package com.lumina_bank.aiassistantservice.domain.intent.impl.account;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.AccountCreateDto;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.AccountResponse;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
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
import java.util.Optional;

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
                        ParseEnumUtil.enumValues(Currency.class),
                        "Account currency."
                ),
                new RequiredParam(
                        "type",
                        ParamType.ENUM,
                        ParseEnumUtil.enumValues(AccountType.class),
                        "Account type."
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

        Optional<Currency> currency =
                ParseEnumUtil.parseEnumSafe(
                        Currency.class,
                        params.get("currency")
                );

        if (currency.isEmpty()) {
            return AssistantExecutionResult.needClarification(
                    intent(),
                    new ClarificationData("INVALID_CURRENCY")
            );
        }

        if (!params.containsKey("type")) {
            return AssistantExecutionResult.askParam(
                    intent(),
                    requiredParams().get(1)
            );
        }

        Optional<AccountType> type =
                ParseEnumUtil.parseEnumSafe(
                        AccountType.class,
                        params.get("type")
                );

        if (type.isEmpty()) {
            return AssistantExecutionResult.needClarification(
                    intent(),
                    new ClarificationData("INVALID_ACCOUNT_TYPE")
            );
        }

        return AssistantExecutionResult.success(intent(), new EmptyData());
    }

    @Override
    public AssistantExecutionResult perform(Map<String, Object> params) {
        try {
            Optional<Currency> currency =
                    ParseEnumUtil.parseEnumSafe(Currency.class, params.get("currency"));

            if (currency.isEmpty()) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("INVALID_CURRENCY")
                );
            }

            Optional<AccountType> type =
                    ParseEnumUtil.parseEnumSafe(AccountType.class, params.get("type"));

            if (type.isEmpty()) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("INVALID_ACCOUNT_TYPE")
                );
            }

            AccountResponse created =
                    accountGateway.createAccount(new AccountCreateDto(currency.get(), type.get()));

            return AssistantExecutionResult.success(
                    intent(),
                    new AccountCreatedData(created)
            );

        } catch (ServiceCallException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage());
        }
    }
}
