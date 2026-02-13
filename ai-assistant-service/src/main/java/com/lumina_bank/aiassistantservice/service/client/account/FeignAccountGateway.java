package com.lumina_bank.aiassistantservice.service.client.account;

import com.lumina_bank.aiassistantservice.domain.dto.client.account.AccountCreateDto;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.AccountResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.CardCreateDto;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.CardResponse;
import com.lumina_bank.aiassistantservice.domain.exception.ExternalServiceException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeignAccountGateway {

    private final AccountClientService client;

    public List<AccountResponse> getUserAccounts() {
        try {
            var response = client.getUserAccounts();

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ExternalServiceException("Account service returned " + response.getStatusCode());
            }

            return Optional.ofNullable(response.getBody()).orElse(List.of());

        } catch (FeignException e) {
            throw new ExternalServiceException("Сервіс рахунків тимчасово недоступний");
        }
    }

    public AccountResponse createAccount(AccountCreateDto dto){
        try {
            var response = client.createAccount(dto);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ExternalServiceException("Account service returned " + response.getStatusCode());
            }
            return response.getBody();

        } catch (FeignException e) {
            throw new ExternalServiceException("Сервіс рахунків тимчасово недоступний");
        }
    }

    public List<CardResponse> getMyCards(){
        try {
            var response = client.getMyCards();

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ExternalServiceException("Account service returned " + response.getStatusCode());
            }

            return Optional.ofNullable(response.getBody()).orElse(List.of());

        } catch (FeignException e) {
            throw new ExternalServiceException("Сервіс рахунків тимчасово недоступний");
        }
    }

    public CardResponse createCard (Long accountId,CardCreateDto dto){
        try {
            var response = client.createCard(accountId,dto);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ExternalServiceException("Account service returned " + response.getStatusCode());
            }
            return response.getBody();

        } catch (FeignException e) {
            throw new ExternalServiceException("Сервіс рахунків тимчасово недоступний");
        }
    }

    public List<CardResponse> getCardsByAccountId(Long accountId){
        try {
            var response = client.getCardsByAccountId(accountId);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ExternalServiceException("Account service returned " + response.getStatusCode());
            }

            return Optional.ofNullable(response.getBody()).orElse(List.of());

        } catch (FeignException e) {
            throw new ExternalServiceException("Сервіс рахунків тимчасово недоступний");
        }
    }
}