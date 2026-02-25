package com.lumina_bank.paymentservice.infrastructure.external.transaction;

import com.lumina_bank.paymentservice.infrastructure.external.transaction.dto.TransactionRequest;
import com.lumina_bank.paymentservice.infrastructure.external.transaction.dto.TransactionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "transaction-service", path = "/transactions")
public interface TransactionClientService {

    @PostMapping("/transfer")
    TransactionResponse makeTransaction(@RequestBody TransactionRequest request);
}