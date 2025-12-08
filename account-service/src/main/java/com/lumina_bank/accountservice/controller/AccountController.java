package com.lumina_bank.accountservice.controller;

import com.lumina_bank.accountservice.dto.AccountCreateDto;
import com.lumina_bank.accountservice.dto.AccountOperationDto;
import com.lumina_bank.accountservice.dto.AccountResponse;
import com.lumina_bank.accountservice.enums.Status;
import com.lumina_bank.accountservice.model.Account;
import com.lumina_bank.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<?> createAccount(@Valid @RequestBody AccountCreateDto accountDto) {
        log.info("POST /accounts - Received request to create account : {}", accountDto.userId());

        Account account = accountService.createAccount(accountDto);

        log.info("Account created id={}", account.getId());

        return ResponseEntity.created(URI.create("/accounts/" + account.getId()))
                .body(AccountResponse.fromEntity(account));
    }

    @PutMapping("/{accountId}/deposit")
    public ResponseEntity<?> deposit(
            @PathVariable Long accountId,
            @Valid @RequestBody AccountOperationDto accountDto) {
        log.info("PUT /accounts/{accountId}/deposit - Received request to deposit to accountId = {}", accountId);

        Account updateAccount = accountService.deposit(accountId, accountDto.amount());

        log.info("Deposit - Account updated id={}", updateAccount.getId());

        return ResponseEntity.ok(AccountResponse.fromEntity(updateAccount));
    }

    @PutMapping("/{accountId}/withdraw")
    public ResponseEntity<?> withdraw(
            @PathVariable Long accountId,
            @Valid @RequestBody AccountOperationDto accountDto) {
        log.info("PUT /accounts/{accountId}/withdraw - Received request to withdraw from accountId = {}", accountId);

        Account updateAccount = accountService.withdraw(accountId, accountDto.amount());

        log.info("Withdraw - Account updated id={}", updateAccount.getId());

        return ResponseEntity.ok(AccountResponse.fromEntity(updateAccount));
    }

    @GetMapping("/{userId}/user-accounts")
    public ResponseEntity<?> getUserAccounts(@PathVariable Long userId) {
        log.info("GET /accounts/{userId}/user-accounts - Fetching user account with userId = {}", userId);

        return ResponseEntity.ok().body(accountService.getAccountsByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccount(@PathVariable Long id) {
        log.info("GET /accounts/id} - Fetching account with id = {}", id);

        return ResponseEntity.ok().body(accountService.getAccountById(id));
    }

    @PatchMapping("/{accountId}")
    public ResponseEntity<?> setActiveAccount(
            @PathVariable Long accountId,
            @RequestBody Status status) {
        log.info("PUT /accounts/{accountId} - Received request to change active status account with id={}", accountId);

        Account account = accountService.setActive(accountId, status);

        log.info("Active status account updated id={},status={}", account.getId(),status);

        return ResponseEntity.ok(AccountResponse.fromEntity(account));
    }
}
