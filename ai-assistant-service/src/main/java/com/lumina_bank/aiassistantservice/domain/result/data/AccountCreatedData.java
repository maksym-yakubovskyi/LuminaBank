package com.lumina_bank.aiassistantservice.domain.result.data;

import com.lumina_bank.aiassistantservice.domain.dto.client.AccountResponse;
import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

public record AccountCreatedData(
        AccountResponse account
) implements AssistantData {
}
