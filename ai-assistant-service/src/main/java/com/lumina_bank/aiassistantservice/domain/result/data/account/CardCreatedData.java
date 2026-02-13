package com.lumina_bank.aiassistantservice.domain.result.data.account;

import com.lumina_bank.aiassistantservice.domain.dto.client.account.CardResponse;
import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

public record CardCreatedData(
        CardResponse card
) implements AssistantData {}
