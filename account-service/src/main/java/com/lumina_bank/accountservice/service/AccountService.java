package com.lumina_bank.accountservice.service;

import com.lumina_bank.accountservice.dto.AccountCreateDto;
import com.lumina_bank.accountservice.dto.AccountResponse;
import com.lumina_bank.accountservice.enums.CountryBankCode;
import com.lumina_bank.accountservice.enums.Status;
import com.lumina_bank.accountservice.enums.UserType;
import com.lumina_bank.accountservice.exception.AccountLockedException;
import com.lumina_bank.accountservice.exception.AccountNotFoundException;
import com.lumina_bank.accountservice.exception.InsufficientBalanceException;
import com.lumina_bank.accountservice.exception.InvalidAmountException;
import com.lumina_bank.accountservice.model.Account;
import com.lumina_bank.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public Account createAccount(AccountCreateDto accountDto) {
        log.debug("Attempting to create account with userId={}", accountDto.userId());

        String iban = generateIban();
        while (accountRepository.existsByIban(iban))
            iban = generateIban();

        //TODO:додати перевірку чи існує юзер та який у нього тип
        Account account = Account.builder().
                userId(accountDto.userId()).
                userType(UserType.INDIVIDUAL).
                balance(BigDecimal.ZERO).
                iban(iban).
                currency(accountDto.currency()).
                status(Status.ACTIVE).
                type(accountDto.type()).
                build();

        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByUserId(Long userId) {
        log.debug("Retrieving accounts with userId={}", userId);

        return accountRepository.findAllByUserId(userId)
                .stream()
                .filter((Account account) -> account.getStatus() == Status.ACTIVE)
                .map(AccountResponse::fromEntity).toList();
    }

    @Transactional
    public Account deposit(Long id, BigDecimal amount) {
        log.debug("Deposit with id={}", id);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        Account account = getAccountById(id);

        if (account.getStatus() != Status.ACTIVE) {
            throw new AccountLockedException("Account is not active");
        }

        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }

    @Transactional
    public Account withdraw(Long id, BigDecimal amount) {
        log.debug("Withdraw with id={}", id);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        Account account = getAccountById(id);

        if (account.getStatus() != Status.ACTIVE) {
            throw new AccountLockedException("Account is not active");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Not enough money to complete transaction");
        }

        account.setBalance(account.getBalance().subtract(amount));
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Account getAccountById(Long accountId) {
        log.debug("Retrieving account with id={}", accountId);

        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account with id " + accountId + " not found"));
    }

    @Transactional(readOnly = true)
    public Account getAccountByIban(String iban) {
        log.debug("Retrieving account with iban={}", iban);

        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundException("Account with iban " + iban + " not found"));
    }

    @Transactional
    public Account setActive(Long accountId, Status status) {
        log.debug("Attempting to change account status accountId={}", accountId);

        Account account = getAccountById(accountId);
        account.setStatus(status);

        if (status.equals(Status.INACTIVE) || status.equals(Status.BLOCKED)) {
            account.getCards().forEach((card) -> card.setStatus(status));
        }

        return accountRepository.save(account);
    }

    private String generateIban() {
        Random random = new Random();
        int controlDigit = random.nextInt(90) + 10;

        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 19; i++) {
            accountNumber.append(random.nextInt(10));
        }
        return CountryBankCode.UA.name() + controlDigit + CountryBankCode.UA.getBankCode() + accountNumber;
    }
}
