package com.lumina_bank.aiassistantservice.domain.assistant.result.data.account;

import com.lumina_bank.aiassistantservice.infrastructure.external.account.dto.AccountResponse;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantData;

public record AccountCreatedData(
        AccountResponse account
) implements AssistantData {}