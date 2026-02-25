package com.lumina_bank.accountservice.api.request;

import com.lumina_bank.accountservice.domain.enums.AccountType;
import com.lumina_bank.common.enums.payment.Currency;
import jakarta.validation.constraints.NotNull;

public record AccountCreateRequest(
        @NotNull Currency currency,
        @NotNull AccountType type
) {}