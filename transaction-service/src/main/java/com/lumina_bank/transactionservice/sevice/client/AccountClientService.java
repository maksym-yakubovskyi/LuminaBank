package com.lumina_bank.transactionservice.sevice.client;

import com.lumina_bank.transactionservice.dto.client.AccountOperationDto;
import com.lumina_bank.transactionservice.dto.client.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "account-service", path = "/accounts")
public interface AccountClientService {

    @PutMapping("/{accountId}/deposit")
    ResponseEntity<AccountResponse> deposit(@PathVariable Long accountId, @RequestBody AccountOperationDto accountOperationDto);

    @PutMapping("/{accountId}/withdraw")
    ResponseEntity<AccountResponse> withdraw(@PathVariable Long accountId, @RequestBody AccountOperationDto accountOperationDto);

}
