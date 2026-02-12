package com.lumina_bank.aiassistantservice.domain.intent.impl;

import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.AccountResponse;
import com.lumina_bank.aiassistantservice.domain.result.data.BalanceData;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.service.client.AccountClientService;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckBalanceIntent implements IntentDefinition {

    private final AccountClientService accountClient;

    @Override
    public Intent intent() {
        return Intent.CHECK_BALANCE;
    }

    @Override
    public List<RequiredParam> requiredParams() {
        return List.of();
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
                    "Не вдалося отримати інформацію про рахунки"
            );
        }

        if (accounts == null || accounts.isEmpty()) {
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
    }
}
