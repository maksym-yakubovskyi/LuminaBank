package com.lumina_bank.accountservice.controller;

import com.lumina_bank.accountservice.dto.LoanApplicationRequest;
import com.lumina_bank.accountservice.dto.LoanOfferResponse;
import com.lumina_bank.accountservice.dto.LoanResponse;
import com.lumina_bank.accountservice.model.Loan;
import com.lumina_bank.accountservice.service.LoanService;
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

    @PostMapping("/offers")
    public ResponseEntity<?> getLoanOffers(
            @RequestBody LoanApplicationRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        if (jwt == null)
            throw new RuntimeException("JWT required");

        Long userId = Long.valueOf(jwt.getSubject());

        List<LoanOfferResponse> offers =
                loanService.generateLoanOffers(
                        userId,
                        request.creditAccountId(),
                        request.requestedAmount()
                );

        return ResponseEntity.ok(offers);
    }

    @PostMapping("/approve")
    public ResponseEntity<?> approveLoan(
            @RequestBody LoanApplicationRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        if (jwt == null)
            throw new RuntimeException("JWT required");

        Long userId = Long.valueOf(jwt.getSubject());

        Loan loan = loanService.approveLoan(userId, request);

        return ResponseEntity.ok(LoanResponse.fromEntity(loan));
    }

    @GetMapping("/my")
    public ResponseEntity<List<LoanResponse>> getMyLoans(
            @AuthenticationPrincipal Jwt jwt
    ) {
        if (jwt == null)
            throw new RuntimeException("JWT required");

        Long userId = Long.valueOf(jwt.getSubject());

        List<Loan> loans = loanService.getActiveLoans(userId);

        List<LoanResponse> response = loans.stream()
                .map(LoanResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/available-credit-accounts")
    public ResponseEntity<?> getAvailableCreditAccounts(
            @AuthenticationPrincipal Jwt jwt
    ) {
        if (jwt == null)
            throw new RuntimeException("JWT required");

        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(
                loanService.getAvailableCreditAccounts(userId)
        );
    }
}