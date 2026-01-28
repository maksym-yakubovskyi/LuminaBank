package com.lumina_bank.transactionservice.sevice.client;

import com.lumina_bank.transactionservice.dto.client.AccountOperationDto;
import com.lumina_bank.transactionservice.dto.client.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "account-service", path = "/accounts")
public interface AccountClientService {

    @PutMapping("/deposit")
    ResponseEntity<AccountResponse> deposit(@RequestBody AccountOperationDto accountOperationDto);

    @PutMapping("/withdraw")
    ResponseEntity<AccountResponse> withdraw(@RequestBody AccountOperationDto accountOperationDto);
}