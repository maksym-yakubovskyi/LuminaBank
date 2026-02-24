package com.lumina_bank.aiassistantservice.infrastructure.external.account;

import com.lumina_bank.aiassistantservice.infrastructure.external.account.dto.*;
import com.lumina_bank.aiassistantservice.infrastructure.security.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "account-service",configuration = FeignSecurityConfig.class)
public interface AccountClientService {

    @GetMapping("/accounts/my")
    List<AccountResponse> getUserAccounts();

    @PostMapping("/accounts")
    AccountResponse createAccount(@RequestBody AccountCreateRequest dto);


    @GetMapping("/cards/my")
    List<CardResponse> getMyCards();

    @PostMapping("/cards/{accountId}")
    CardResponse createCard(@PathVariable Long accountId, @RequestBody CardCreateRequest dto);

    @GetMapping("/cards/{accountId}")
    List<CardResponse> getCardsByAccountId(@PathVariable Long accountId);


    @PostMapping("/accounts/loans/offers")
    List<LoanOfferResponse> getLoanOffers(@RequestBody LoanApplicationRequest request);

    @PostMapping("/accounts/loans/approve")
    LoanResponse approveLoan(@RequestBody LoanApplicationRequest request);

    @GetMapping("/accounts/loans/my")
    List<LoanResponse> getMyLoans();

    @GetMapping("/accounts/loans/available-credit-accounts")
    List<AccountResponse> getAvailableCreditAccounts();
}