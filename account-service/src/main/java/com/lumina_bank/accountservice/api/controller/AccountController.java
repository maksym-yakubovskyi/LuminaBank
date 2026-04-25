package com.lumina_bank.accountservice.api.controller;

import com.lumina_bank.accountservice.api.request.AccountCreateRequest;
import com.lumina_bank.accountservice.api.request.AccountOperationRequest;
import com.lumina_bank.accountservice.api.response.AccountResponse;
import com.lumina_bank.accountservice.application.mapper.AccountMapper;
import com.lumina_bank.accountservice.domain.enums.Status;
import com.lumina_bank.accountservice.domain.model.Account;
import com.lumina_bank.accountservice.application.service.AccountService;
import com.lumina_bank.common.security.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody AccountCreateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = JwtUtils.extractUserId(jwt);

        Account account = accountService.createAccount(request,userId);

        log.info("Account created accountId={} userId={}", account.getId(), userId);

        return ResponseEntity.created(URI.create("/accounts/" + account.getId()))
                .body(accountMapper.toResponse(account));
    }

    @PutMapping("/deposit")
    public ResponseEntity<AccountResponse> deposit(@Valid @RequestBody AccountOperationRequest request) {
        Account account = accountService.deposit(request.cardNumber(),request.amount());

        log.info("Deposit completed accountId={} amount={}", account.getId(), request.amount());

        return ResponseEntity.ok(accountMapper.toResponse(account));
    }

    @PutMapping("/withdraw")
    public ResponseEntity<AccountResponse> withdraw(@Valid @RequestBody AccountOperationRequest request) {
        Account account = accountService.withdraw(request.cardNumber(), request.amount());

        log.info("Withdraw completed accountId={} amount={}", account.getId(), request.amount());

        return ResponseEntity.ok(accountMapper.toResponse(account));
    }

    @GetMapping("/my")
    public ResponseEntity<List<AccountResponse>> getMyAccounts(@AuthenticationPrincipal Jwt jwt) {
        Long userId = JwtUtils.extractUserId(jwt);

        List<Account> accounts = accountService.getAccountsByUserIdActive(userId);

        log.debug("Fetched accounts for userId={} count={}", userId, accounts.size());

        return ResponseEntity.ok().body(accountMapper.toResponseList(accounts));
    }

    @GetMapping("/by-card/{cardNumber}")
    public ResponseEntity<AccountResponse> getByCard(@PathVariable String cardNumber) {
        Account account = accountService.getAccountByCardNumber(cardNumber);

        log.debug("Fetched account by card accountId={}", account.getId());

        return ResponseEntity.ok().body(accountMapper.toResponse(account));
    }

    @GetMapping("/merchant/card-number/{providerId}")
    public ResponseEntity<String> getMerchantCard(@PathVariable Long providerId) {
        String response = accountService.getMerchantCardNumber(providerId);

        log.info("Merchant card fetched providerId={}", providerId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{accountId}/status")
    public ResponseEntity<AccountResponse> changeStatus(@PathVariable Long accountId, @RequestBody Status status) {
        Account account = accountService.setStatus(accountId, status);

        log.info("Account status changed accountId={} newStatus={}", accountId, status);

        return ResponseEntity.ok(accountMapper.toResponse(account));
    }

    @GetMapping("/admin/user/{userId}")
    public ResponseEntity<?> getUserAccounts(@PathVariable Long userId) {
        List<Account> accounts = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok().body(accountMapper.toResponseList(accounts));
    }
}