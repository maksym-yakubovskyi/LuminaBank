package com.lumina_bank.aiassistantservice.domain.result.data.account;

import com.lumina_bank.aiassistantservice.domain.dto.client.account.AccountResponse;
import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

public record AccountCreatedData(
        AccountResponse account
) implements AssistantData {
}
