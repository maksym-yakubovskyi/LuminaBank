package com.lumina_bank.paymentservice.service.client;

import com.lumina_bank.paymentservice.dto.client.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "account-service", path = "/accounts")
public interface AccountClientService {

    @GetMapping("/{id}")
    ResponseEntity<AccountResponse> getAccount(@PathVariable Long id);
}
