package com.lumina_bank.aiassistantservice.domain.assistant.result.data.account;

import com.lumina_bank.aiassistantservice.infrastructure.external.account.dto.AccountResponse;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantData;

import java.util.List;

public record AccountsListData(
        List<AccountResponse> accounts
) implements AssistantData {}

