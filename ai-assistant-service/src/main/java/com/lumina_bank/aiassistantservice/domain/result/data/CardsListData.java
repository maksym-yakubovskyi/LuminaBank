package com.lumina_bank.aiassistantservice.domain.result.data;

import com.lumina_bank.aiassistantservice.domain.dto.client.CardResponse;
import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

import java.util.List;

public record CardsListData(
        List<CardResponse> cards
) implements AssistantData {}

