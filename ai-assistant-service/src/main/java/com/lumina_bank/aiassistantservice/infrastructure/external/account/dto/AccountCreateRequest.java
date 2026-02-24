package com.lumina_bank.aiassistantservice.infrastructure.external.account.dto;

import com.lumina_bank.aiassistantservice.infrastructure.external.account.enums.AccountType;
import com.lumina_bank.common.enums.payment.Currency;

public record AccountCreateRequest(
        Currency currency,
        AccountType type
) {}