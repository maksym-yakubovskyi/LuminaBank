package com.lumina_bank.aiassistantservice.domain.result.data;

import com.lumina_bank.aiassistantservice.domain.dto.client.CardResponse;
import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

public record CardCreatedData(
        CardResponse card
) implements AssistantData {}
