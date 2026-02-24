package com.lumina_bank.paymentservice.infrastructure.external.transaction;

import com.lumina_bank.common.exception.ServiceCallException;
import com.lumina_bank.paymentservice.infrastructure.external.transaction.dto.TransactionRequest;
import com.lumina_bank.paymentservice.infrastructure.external.transaction.dto.TransactionResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeignTransactionGateway {
    private final TransactionClientService client;

    public TransactionResponse execute(TransactionRequest request) {
        try{
            return client.makeTransaction(request);
        }catch (FeignException e){
            throw new ServiceCallException("Account service failed", e);
        }
    }
}
