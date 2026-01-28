package com.lumina_bank.paymentservice.service.client;

import com.lumina_bank.paymentservice.dto.client.TransactionRequest;
import com.lumina_bank.paymentservice.dto.client.TransactionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "transaction-service", path = "/transactions")
public interface TransactionClientService {

    @PostMapping("/transfer")
    ResponseEntity<TransactionResponse> makeTransaction(@RequestBody TransactionRequest request);
}