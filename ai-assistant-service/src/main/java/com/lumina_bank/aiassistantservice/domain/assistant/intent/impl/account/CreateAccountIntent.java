package com.lumina_bank.aiassistantservice.domain.assistant.intent.impl.account;

import com.lumina_bank.aiassistantservice.domain.assistant.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.assistant.RequiredParam;
import com.lumina_bank.aiassistantservice.infrastructure.external.account.dto.AccountCreateRequest;
import com.lumina_bank.aiassistantservice.infrastructure.external.account.dto.AccountResponse;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.account.AccountCreatedData;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.EmptyData;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.assistant.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.infrastructure.external.account.FeignAccountGateway;
import com.lumina_bank.aiassistantservice.domain.util.ParseEnumUtil;
import com.lumina_bank.aiassistantservice.infrastructure.external.account.enums.AccountType;
import com.lumina_bank.common.enums.payment.Currency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
    public List<RequiredParam> requiredParams(AssistantContext context) {
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
                        allowedAccountTypes(context),
                        "Account type."
                )
        );
    }

    @Override
    public boolean requiresFinalConfirmation() {
        return true;
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params, UUID conversationId, AssistantContext context) {
        List<RequiredParam> schema = requiredParams(context);

        if (!params.containsKey("currency")) {
            return AssistantExecutionResult.askParam(
                    intent(),
                    schema.getFirst()
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
                    schema.get(1)
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

        if (!allowedAccountTypes(context).contains(type.get().name())) {
            return AssistantExecutionResult.needClarification(
                    intent(),
                    new ClarificationData("ACCOUNT_TYPE_NOT_ALLOWED_FOR_ROLE")
            );
        }

        return AssistantExecutionResult.success(
                intent(),
                new EmptyData()
        );
    }

    @Override
    public AssistantExecutionResult perform(Map<String, Object> params,AssistantContext context) {
        try {
            Currency currency = Currency.valueOf(params.get("currency").toString());
            AccountType type = AccountType.valueOf(params.get("type").toString());

            if (!allowedAccountTypes(context).contains(type.name())) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("ACCOUNT_TYPE_NOT_ALLOWED_FOR_ROLE")
                );
            }

            AccountResponse created =
                    accountGateway.createAccount(
                            new AccountCreateRequest(currency, type)
                    );

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
    private List<String> allowedAccountTypes(AssistantContext context) {

        if (context.isBusiness()) {
            return List.of(
                    AccountType.MERCHANT.name(),
                    AccountType.CREDIT.name()
            );
        }

        return List.of(
                AccountType.DEBIT.name(),
                AccountType.CREDIT.name()
        );
    }
}
