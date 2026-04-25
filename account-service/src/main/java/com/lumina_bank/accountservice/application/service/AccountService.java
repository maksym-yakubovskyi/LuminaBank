package com.lumina_bank.accountservice.application.service;

import com.lumina_bank.accountservice.domain.exception.*;
import com.lumina_bank.accountservice.api.request.AccountCreateRequest;
import com.lumina_bank.accountservice.domain.enums.AccountType;
import com.lumina_bank.accountservice.domain.enums.CountryBankCode;
import com.lumina_bank.accountservice.domain.enums.Status;
import com.lumina_bank.accountservice.domain.model.Account;
import com.lumina_bank.accountservice.domain.model.Card;
import com.lumina_bank.accountservice.domain.repository.AccountRepository;
import com.lumina_bank.accountservice.domain.repository.CardRepository;
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
    private final CardRepository cardRepository;
    private final LoanPaymentService loanPaymentService;

    @Transactional
    public Account createAccount(AccountCreateRequest request, Long userId) {
        String iban = generateUniqueIban();

        Account account = Account.builder().
                userId(userId).
                balance(BigDecimal.ZERO).
                iban(iban).
                currency(request.currency()).
                status(Status.ACTIVE).
                type(request.type()).
                build();

        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public List<Account> getAccountsByUserIdActive(Long userId) {
        return accountRepository.findAllByUserIdAndStatusOrderByCreatedAtAsc(userId, Status.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findAllByUserIdOrderByCreatedAtAsc(userId);
    }

    @Transactional(readOnly = true)
    public Account getAccountByCardNumber(String cardNumber) {
        return cardRepository.findAccountByCardNumber(cardNumber)
                .orElseThrow(() -> new CardNotFoundException("Card not found: " + cardNumber));
    }

    @Transactional
    public Account deposit(String cardNumber, BigDecimal amount) {
        validateAmount(amount);

        Card card = getActiveCard(cardNumber);
        Account account = getActiveAccount(card.getAccount());

        account.setBalance(account.getBalance().add(amount));

        if (account.getType() == AccountType.CREDIT) {
            loanPaymentService.processCreditAccountPayment(account, amount);
        }

        return account;
    }

    @Transactional
    public Account withdraw(String cardNumber, BigDecimal amount) {
        validateAmount(amount);

        Card card = getActiveCard(cardNumber);

        if (card.getLimit() != null && amount.compareTo(card.getLimit()) > 0) {
            throw new InvalidAmountException("Amount exceeds card limit");
        }

        Account account = getActiveAccount(card.getAccount());

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Not enough money to complete transaction");
        }

        account.setBalance(account.getBalance().subtract(amount));

        return account;
    }

    @Transactional
    public Account setStatus(Long accountId, Status status) {
        log.debug("Attempting to change account status accountId={}", accountId);

        Account account = getAccountById(accountId);
        account.setStatus(status);

        if (status.equals(Status.INACTIVE) || status.equals(Status.BLOCKED)) {
            account.getCards().forEach((card) -> card.setStatus(status));
        }

        return account;
    }

    @Transactional(readOnly = true)
    public Account getAccountById(Long accountId) {
        log.debug("Retrieving account with id={}", accountId);

        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account with id " + accountId + " not found"));
    }

    @Transactional(readOnly = true)
    public String getMerchantCardNumber(Long providerId) {
        Account merchantAccount = accountRepository
                .findByUserIdAndType(providerId, AccountType.MERCHANT)
                .orElseThrow(() -> new AccountNotFoundException("Merchant account not found for providerId=" + providerId));

        Card merchantCard = cardRepository
                .findFirstByAccountAndStatus(merchantAccount, Status.ACTIVE)
                .orElseThrow(() -> new CardNotFoundException("Active merchant card not found for providerId=" + providerId));

        return merchantCard.getCardNumber();
    }

    @Transactional(readOnly = true)
    public Account getCreditAccountById(Long userId, Long accountId) {
        Account account = accountRepository.findByIdAndUserIdAndStatus(accountId, userId,Status.ACTIVE)
                .orElseThrow(() -> new AccountNotFoundException("Account with id " + accountId + " not found"));

        if(account.getType() != AccountType.CREDIT) throw new IllegalArgumentException("Only CREDIT accounts allowed");
        return account;
    }

    @Transactional(readOnly = true)
    public List<Account> getCreditAccountsEntities(Long userId){
        return accountRepository.findAllByUserIdAndStatusAndType(userId,Status.ACTIVE,AccountType.CREDIT);
    }

    @Transactional
    public void creditAccount(Long accountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidAmountException("Amount must be greater than zero");

        Account account = getAccountById(accountId);

        if (account.getStatus() != Status.ACTIVE)
            throw new AccountLockedException("Account is not active");

        account.setBalance(account.getBalance().add(amount));
    }

    // PRIVATE HELPERS

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }
    }

    private Card getActiveCard(String cardNumber) {
        Card card = cardRepository.findByCardNumberWithAccount(cardNumber)
                .orElseThrow(() ->
                        new CardNotFoundException("Card not found: " + cardNumber));

        if (card.getStatus() != Status.ACTIVE) {
            throw new AccountLockedException("Card is not active");
        }

        return card;
    }

    private Account getActiveAccount(Account account) {
        if (account.getStatus() != Status.ACTIVE) {
            throw new AccountLockedException("Account is not active");
        }
        return account;
    }

    private String generateUniqueIban() {
        String iban;
        do {
            iban = generateIban();
        } while (accountRepository.existsByIban(iban));
        return iban;
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