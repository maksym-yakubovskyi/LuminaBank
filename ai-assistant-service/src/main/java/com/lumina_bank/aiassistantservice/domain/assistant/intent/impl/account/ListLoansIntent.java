package com.lumina_bank.aiassistantservice.domain.assistant.intent.impl.account;

import com.lumina_bank.aiassistantservice.domain.assistant.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.assistant.RequiredParam;
import com.lumina_bank.aiassistantservice.infrastructure.external.account.dto.LoanResponse;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.assistant.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.account.LoanListData;
import com.lumina_bank.aiassistantservice.infrastructure.external.account.FeignAccountGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ListLoansIntent implements IntentDefinition {
    private final FeignAccountGateway accountGateway;

    @Override
    public Intent intent() {
        return Intent.LIST_LOANS;
    }

    @Override
    public List<RequiredParam> requiredParams(AssistantContext context) {
        return List.of();
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params, UUID conversationId, AssistantContext context) {
        try{
            List<LoanResponse> loans = accountGateway.getMyLoans();

            return AssistantExecutionResult.success(
                    intent(),
                    new LoanListData(loans)
            );
        } catch (ServiceCallException e) {
            return AssistantExecutionResult.error(intent(), e.getMessage());
        }
    }
}
