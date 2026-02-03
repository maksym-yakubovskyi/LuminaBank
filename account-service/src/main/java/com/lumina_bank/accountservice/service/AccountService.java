package com.lumina_bank.accountservice.service;

import com.lumina_bank.accountservice.dto.AccountCreateDto;
import com.lumina_bank.accountservice.dto.AccountResponse;
import com.lumina_bank.accountservice.dto.MerchantCardResponse;
import com.lumina_bank.accountservice.dto.client.UserCheckResponse;
import com.lumina_bank.accountservice.enums.AccountType;
import com.lumina_bank.accountservice.enums.CountryBankCode;
import com.lumina_bank.accountservice.enums.Status;
import com.lumina_bank.accountservice.exception.*;
import com.lumina_bank.accountservice.model.Account;
import com.lumina_bank.accountservice.model.Card;
import com.lumina_bank.accountservice.repository.AccountRepository;
import com.lumina_bank.accountservice.repository.CardRepository;
import com.lumina_bank.accountservice.service.client.UserServiceClient;
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
    private final UserServiceClient userServiceClient;

    @Transactional
    public Account createAccount(AccountCreateDto accountDto,Long userId) {
        log.debug("Attempting to create account with userId={}", userId);

        UserCheckResponse userCheck = checkUser(userId);

        String iban = generateIban();
        while (accountRepository.existsByIban(iban))
            iban = generateIban();

        Account account = Account.builder().
                userId(userId).
                userType(userCheck.userType()).
                balance(BigDecimal.ZERO).
                iban(iban).
                currency(accountDto.currency()).
                status(Status.ACTIVE).
                type(accountDto.type()).
                build();

        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public MerchantCardResponse getMerchantCardNumber(Long providerId) {
        Account merchantAccount = accountRepository
                .findByUserIdAndType(providerId, AccountType.MERCHANT)
                .orElseThrow(() -> new AccountNotFoundException("Merchant account not found for providerId=" + providerId));

        Card merchantCard = cardRepository
                .findFirstByAccountAndStatus(merchantAccount, Status.ACTIVE)
                .orElseThrow(() -> new CardNotFoundException("Active merchant card not found for providerId=" + providerId));

        return new MerchantCardResponse(merchantCard.getCardNumber());
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByUserId(Long userId) {
        log.debug("Retrieving accounts with userId={}", userId);

        return accountRepository.findAllByUserIdAndStatus(userId,Status.ACTIVE).stream()
                .map(AccountResponse::fromEntity).toList();
    }

    @Transactional
    public Account deposit(String cardNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidAmountException("Amount must be greater than zero");

        Card card = cardRepository.findByCardNumberWithAccount(cardNumber)
                .orElseThrow(() -> new CardNotFoundException("Card not found: " + cardNumber));

        if (card.getStatus() != Status.ACTIVE) {
            throw new AccountLockedException("Card is not active");
        }

        Account account = card.getAccount();

        if (account.getStatus() != Status.ACTIVE) {
            throw new AccountLockedException("Account is not active");
        }

        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }

    @Transactional
    public Account withdraw(String cardNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidAmountException("Amount must be greater than zero");

        Card card = cardRepository.findByCardNumberWithAccount(cardNumber)
                .orElseThrow(() -> new CardNotFoundException("Card not found: " + cardNumber));

        if (card.getStatus() != Status.ACTIVE) {
            throw new AccountLockedException("Card is not active");
        }
        if (card.getLimit() != null && amount.compareTo(card.getLimit()) > 0) {
            throw new InvalidAmountException("Amount exceeds card limit");
        }

        Account account = card.getAccount();

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

    @Transactional
    public Account setStatus(Long accountId, Status status) {
        log.debug("Attempting to change account status accountId={}", accountId);

        Account account = getAccountById(accountId);
        account.setStatus(status);

        if (status.equals(Status.INACTIVE) || status.equals(Status.BLOCKED)) {
            account.getCards().forEach((card) -> card.setStatus(status));
        }

        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountByCardNumber(String cardNumber) {
        Account account = cardRepository.findAccountByCardNumber(cardNumber)
                .orElseThrow(() -> new CardNotFoundException("Card not found: " + cardNumber));

        return AccountResponse.fromEntity(account);
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

    private UserCheckResponse checkUser(Long userId) {
        try{
            var response = userServiceClient.checkUser(userId);

            if (response == null) {
                log.warn("Null response from user service ");
                throw new ExternalServiceException("Null response from user service");
            }
            if (response.getStatusCode().is2xxSuccessful()) {
                UserCheckResponse body = response.getBody();
                if (body != null && body.userType() != null) {
                    if (!body.exists()) {
                        throw new ExternalServiceException("User not found id=" + userId);
                    }
                    if (!body.active()) {
                        throw new ExternalServiceException("User is inactive id=" + userId);
                    }
                    return body;
                } else {
                    log.warn("Empty or invalid user response body");
                    throw  new ExternalServiceException("Empty or invalid user response body");
                }
            } else {
                log.warn("User service returned non-2xx  status={}, body={}",
                        response.getStatusCode(), response.getBody());
                throw new ExternalServiceException("User service error: " + response.getStatusCode());
            }
        }catch (Exception e) {
            log.warn("Failed to fetch user : {}", e.getMessage(), e);
            throw new ExternalServiceException("Failed to get user", e);
        }
    }
}