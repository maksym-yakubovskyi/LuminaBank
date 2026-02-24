package com.lumina_bank.transactionservice.infrastructure.external.account;

import com.lumina_bank.transactionservice.infrastructure.external.account.dto.AccountOperationRequest;
import com.lumina_bank.transactionservice.infrastructure.external.account.dto.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "account-service", path = "/accounts")
public interface AccountClientService {

    @PutMapping("/deposit")
    AccountResponse deposit(@RequestBody AccountOperationRequest request);

    @PutMapping("/withdraw")
    AccountResponse withdraw(@RequestBody AccountOperationRequest request);
}