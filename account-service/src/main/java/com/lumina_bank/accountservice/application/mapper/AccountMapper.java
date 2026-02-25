package com.lumina_bank.accountservice.application.mapper;

import com.lumina_bank.accountservice.api.response.AccountResponse;
import com.lumina_bank.accountservice.domain.model.Account;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountMapper {

    public AccountResponse toResponse(Account account) {
        if (account == null) return null;

        return AccountResponse.builder()
                .id(account.getId())
                .userId(account.getUserId())
                .balance(account.getBalance())
                .iban(account.getIban())
                .currency(account.getCurrency())
                .status(account.getStatus())
                .type(account.getType())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    public List<AccountResponse> toResponseList(List<Account> accounts) {
        return accounts.stream()
                .map(this::toResponse)
                .toList();
    }
}
