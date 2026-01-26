package com.lumina_bank.paymentservice.service.client;

import com.lumina_bank.paymentservice.dto.client.AccountResponse;
import com.lumina_bank.paymentservice.dto.client.MerchantCardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "account-service", path = "/accounts")
public interface AccountClientService {

    @GetMapping("/by-card/{cardNumber}")
    ResponseEntity<AccountResponse> getAccountByCardNumber(@PathVariable String cardNumber);

    @GetMapping("/merchant/card-number/{providerId}")
    ResponseEntity<MerchantCardResponse> getMerchantCardNumber(@PathVariable Long providerId);
}