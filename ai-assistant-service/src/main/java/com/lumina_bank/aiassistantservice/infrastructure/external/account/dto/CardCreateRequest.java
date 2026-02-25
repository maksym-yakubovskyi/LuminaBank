package com.lumina_bank.aiassistantservice.infrastructure.external.account.dto;

import com.lumina_bank.aiassistantservice.infrastructure.external.account.enums.CardNetwork;
import com.lumina_bank.aiassistantservice.infrastructure.external.account.enums.CardType;

import java.math.BigDecimal;

public record CardCreateRequest(
        CardType cardType,
        CardNetwork cardNetwork,
        BigDecimal limit
) {
}
