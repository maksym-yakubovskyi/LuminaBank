package com.lumina_bank.aiassistantservice.domain.dto.client.account;

import com.lumina_bank.common.enums.account.AccountType;
import com.lumina_bank.common.enums.payment.Currency;

public record AccountCreateDto(
        Currency currency,
        AccountType type
) {
}
