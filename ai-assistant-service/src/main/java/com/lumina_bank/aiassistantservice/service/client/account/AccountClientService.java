package com.lumina_bank.aiassistantservice.service.client.account;

import com.lumina_bank.aiassistantservice.domain.dto.client.account.AccountCreateDto;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.AccountResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.CardCreateDto;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.CardResponse;
import com.lumina_bank.aiassistantservice.security.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "account-service",configuration = FeignSecurityConfig.class)
public interface AccountClientService {

    @GetMapping("/accounts/my")
    ResponseEntity<List<AccountResponse>> getUserAccounts();

    @PostMapping("/accounts")
    ResponseEntity<AccountResponse> createAccount(@RequestBody AccountCreateDto dto);

    @GetMapping("/cards/my")
    ResponseEntity<List<CardResponse>> getMyCards();

    @PostMapping("/cards/{accountId}")
    ResponseEntity<CardResponse> createCard(@PathVariable Long accountId, @RequestBody CardCreateDto dto);

    @GetMapping("/cards/{accountId}")
    ResponseEntity<List<CardResponse>> getCardsByAccountId(@PathVariable Long accountId);

}