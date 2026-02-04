package com.lumina_bank.accountservice.controller;

import com.lumina_bank.accountservice.dto.AccountCreateDto;
import com.lumina_bank.accountservice.dto.AccountOperationDto;
import com.lumina_bank.accountservice.dto.AccountResponse;
import com.lumina_bank.accountservice.enums.Status;
import com.lumina_bank.common.exception.JwtMissingException;
import com.lumina_bank.accountservice.model.Account;
import com.lumina_bank.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<?> createAccount(
            @Valid @RequestBody AccountCreateDto accountDto,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("POST /accounts - Received request to create account");

        if (jwt == null) throw new JwtMissingException("JWT token is required");
        Long authUserId = Long.valueOf(jwt.getSubject());

        Account account = accountService.createAccount(accountDto,authUserId);

        log.info("Account created id={}", account.getId());

        return ResponseEntity.created(URI.create("/accounts/" + account.getId()))
                .body(AccountResponse.fromEntity(account));
    }

    @PutMapping("/deposit")
    public ResponseEntity<?> deposit(
            @Valid @RequestBody AccountOperationDto accountDto) {
        log.info("PUT /accounts/deposit - Received request to deposit ");

        Account updateAccount = accountService.deposit(accountDto.cardNumber(),accountDto.amount());

        log.info("Deposit - Account updated id={}", updateAccount.getId());

        return ResponseEntity.ok(AccountResponse.fromEntity(updateAccount));
    }

    @PutMapping("/withdraw")
    public ResponseEntity<?> withdraw(
            @Valid @RequestBody AccountOperationDto accountDto) {
        log.info("PUT /accounts/withdraw - Received request to withdraw");

        Account updateAccount = accountService.withdraw(accountDto.cardNumber(), accountDto.amount());

        log.info("Withdraw - Account updated id={}", updateAccount.getId());

        return ResponseEntity.ok(AccountResponse.fromEntity(updateAccount));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getUserAccounts(@AuthenticationPrincipal Jwt jwt) {
        log.info("GET /accounts/my - Fetching user account");

        if (jwt == null) throw new JwtMissingException("JWT token is required");
        Long authUserId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok().body(accountService.getAccountsByUserId(authUserId));
    }

    @GetMapping("/by-card/{cardNumber}")
    public ResponseEntity<?> getAccount(@PathVariable String cardNumber) {
        log.info("GET /accounts/by-card/{cardNumber} - Fetching account by card number");

        return ResponseEntity.ok().body(accountService.getAccountByCardNumber(cardNumber));
    }

    @GetMapping("/merchant/card-number/{providerId}")
    public ResponseEntity<?> getMerchantCardNumber(@PathVariable Long providerId) {
        log.info("GET /accounts/merchant/card-number/{providerId} - Fetching merchant card, providerId={}", providerId);

        return ResponseEntity.ok(accountService.getMerchantCardNumber(providerId));
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<?> setStatusAccount(
            @PathVariable Long accountId,
            @RequestBody Status status) {
        log.info("PUT /accounts/{accountId} - Received request to change status account with id={}", accountId);

        Account account = accountService.setStatus(accountId, status);

        log.info("Status account updated id={},status={}", account.getId(),status);

        return ResponseEntity.ok(AccountResponse.fromEntity(account));
    }
}