package com.lumina_bank.aiassistantservice.domain.assistant.result.data.account;

import com.lumina_bank.aiassistantservice.infrastructure.external.account.dto.CardResponse;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantData;

public record CardCreatedData(
        CardResponse card
) implements AssistantData {}
