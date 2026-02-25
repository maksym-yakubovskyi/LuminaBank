package com.lumina_bank.paymentservice.infrastructure.external.account;

import com.lumina_bank.paymentservice.infrastructure.external.account.dto.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "account-service", path = "/accounts")
public interface AccountClientService {

    @GetMapping("/by-card/{cardNumber}")
    AccountResponse getAccountByCardNumber(@PathVariable String cardNumber);

    @GetMapping("/merchant/card-number/{providerId}")
    String getMerchantCardNumber(@PathVariable Long providerId);
}