package com.lumina_bank.aiassistantservice.domain.intent.impl.account;

import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.AccountResponse;
import com.lumina_bank.aiassistantservice.domain.result.data.ConfirmationData;
import com.lumina_bank.aiassistantservice.domain.result.data.account.BalanceData;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.service.client.account.FeignAccountGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckBalanceIntent implements IntentDefinition {

    private final FeignAccountGateway accountGateway;

    @Override
    public Intent intent() {
        return Intent.CHECK_BALANCE;
    }

    @Override
    public List<RequiredParam> requiredParams() {
        return List.of();
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params) {
        try {
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

            return AssistantExecutionResult.success(
                    intent(),
                    new BalanceData(accounts)
            );

        }catch (ServiceCallException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage());
        }
    }
}
