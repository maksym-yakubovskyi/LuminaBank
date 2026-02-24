package com.lumina_bank.aiassistantservice.domain.assistant.result.data.account;

import com.lumina_bank.aiassistantservice.infrastructure.external.account.dto.CardResponse;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantData;

import java.util.List;

public record CardsListData(
        List<CardResponse> cards
) implements AssistantData {}

