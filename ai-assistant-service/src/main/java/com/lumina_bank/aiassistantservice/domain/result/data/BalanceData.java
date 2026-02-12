package com.lumina_bank.aiassistantservice.domain.result.data;

import com.lumina_bank.aiassistantservice.domain.dto.client.AccountResponse;
import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

import java.util.List;

public record BalanceData(
        List<AccountResponse> accounts
) implements AssistantData {
}
