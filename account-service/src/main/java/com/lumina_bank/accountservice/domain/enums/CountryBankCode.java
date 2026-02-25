package com.lumina_bank.accountservice.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CountryBankCode {
    UA("305299");

    private final String bankCode;
}