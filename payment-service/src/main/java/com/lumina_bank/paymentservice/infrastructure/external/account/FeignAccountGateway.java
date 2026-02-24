package com.lumina_bank.paymentservice.infrastructure.external.account;

import com.lumina_bank.common.exception.ServiceCallException;
import com.lumina_bank.paymentservice.infrastructure.external.account.dto.AccountResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeignAccountGateway {

    private final AccountClientService client;

    public AccountResponse getByCard(String card){
        try{
            return client.getAccountByCardNumber(card);
        }catch (FeignException e){
            throw new ServiceCallException("Account service failed", e);
        }
    }

    public String getMerchantCard(Long providerId) {
        try{
            return client.getMerchantCardNumber(providerId);
        }catch (FeignException e){
            throw new ServiceCallException("Account service failed", e);
        }
    }
}

