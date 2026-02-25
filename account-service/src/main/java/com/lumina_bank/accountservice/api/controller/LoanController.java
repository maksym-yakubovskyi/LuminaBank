package com.lumina_bank.accountservice.api.controller;

import com.lumina_bank.accountservice.api.request.LoanApplicationRequest;
import com.lumina_bank.accountservice.api.response.AccountResponse;
import com.lumina_bank.accountservice.api.response.LoanOfferResponse;
import com.lumina_bank.accountservice.api.response.LoanResponse;
import com.lumina_bank.accountservice.application.mapper.AccountMapper;
import com.lumina_bank.accountservice.application.mapper.LoanMapper;
import com.lumina_bank.accountservice.domain.model.Account;
import com.lumina_bank.accountservice.domain.model.Loan;
import com.lumina_bank.accountservice.application.service.LoanService;
import com.lumina_bank.common.security.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts/loans")
@RequiredArgsConstructor
@Slf4j
public class LoanController {
    private final LoanService loanService;
    private final LoanMapper loanMapper;
    private final AccountMapper accountMapper;

    @PostMapping("/offers")
    public ResponseEntity<List<LoanOfferResponse>> getLoanOffers(
            @Valid @RequestBody LoanApplicationRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = JwtUtils.extractUserId(jwt);

        List<LoanOfferResponse> offers =
                loanService.generateLoanOffers(
                        userId,
                        request.creditAccountId(),
                        request.requestedAmount()
                );

        log.info("Loan offers generated userId={} accountId={} amount={}",
                userId,
                request.creditAccountId(),
                request.requestedAmount());

        return ResponseEntity.ok(offers);
    }

    @PostMapping("/approve")
    public ResponseEntity<LoanResponse> approveLoan(
            @Valid @RequestBody LoanApplicationRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = JwtUtils.extractUserId(jwt);

        Loan loan = loanService.approveLoan(userId, request);

        log.info("Loan approved loanId={} userId={} amount={} term={}",
                loan.getId(),
                userId,
                loan.getPrincipalAmount(),
                loan.getTermMonths());

        return ResponseEntity.ok(loanMapper.toResponse(loan));
    }

    @GetMapping("/my")
    public ResponseEntity<List<LoanResponse>> getMyLoans(@AuthenticationPrincipal Jwt jwt) {
        Long userId = JwtUtils.extractUserId(jwt);

        List<Loan> loans = loanService.getActiveLoans(userId);

        log.debug("Fetched active loans userId={} count={}", userId, loans.size());

        return ResponseEntity.ok(loanMapper.toResponseList(loans));
    }

    @GetMapping("/available-credit-accounts")
    public ResponseEntity<List<AccountResponse>> getAvailableCreditAccounts(@AuthenticationPrincipal Jwt jwt) {
        Long userId = JwtUtils.extractUserId(jwt);

        List<Account> accounts =
                loanService.getAvailableCreditAccounts(userId);

        return ResponseEntity.ok(accountMapper.toResponseList(accounts));
    }
}