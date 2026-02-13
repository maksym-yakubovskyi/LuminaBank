package com.lumina_bank.aiassistantservice.domain.intent.impl.account;

import com.lumina_bank.aiassistantservice.domain.exception.ExternalServiceException;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.AccountResponse;
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
                return AssistantExecutionResult.needConfirmation(
                        intent(),
                        "У вас ще немає рахунків. Хочете створити рахунок?",
                        Intent.CREATE_ACCOUNT
                );
            }

            return AssistantExecutionResult.success(
                    intent(),
                    new BalanceData(accounts)
            );

        }catch (ExternalServiceException e) {
            return AssistantExecutionResult.error(intent(),
                    "Не вдалося отримати інформацію про рахунки");
        }
    }
}
