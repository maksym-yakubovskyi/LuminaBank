package com.lumina_bank.aiassistantservice.service.client.account;

import com.lumina_bank.aiassistantservice.domain.dto.client.account.*;
import com.lumina_bank.aiassistantservice.util.FeignExceptionMapper;
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
    private final FeignExceptionMapper mapper;

    public List<AccountResponse> getUserAccounts() {
        try {
            return Optional.ofNullable(client.getUserAccounts().getBody())
                    .orElse(List.of());
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public AccountResponse createAccount(AccountCreateDto dto){
        try {
            return client.createAccount(dto).getBody();
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }


    public List<CardResponse> getMyCards(){
        try {
            return Optional.ofNullable(client.getMyCards().getBody())
                    .orElse(List.of());
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public CardResponse createCard (Long accountId,CardCreateDto dto){
        try {
            return client.createCard(accountId,dto).getBody();
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public List<CardResponse> getCardsByAccountId(Long accountId){
        try {
            return Optional.ofNullable(client.getCardsByAccountId(accountId).getBody())
                    .orElse(List.of());
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public List<LoanOfferResponse> getLoanOffers (LoanApplicationRequest request){
        try {
            return Optional.ofNullable(client.getLoanOffers(request).getBody())
                    .orElse(List.of());
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public LoanResponse approveLoan (LoanApplicationRequest request){
        try {
            return client.approveLoan(request).getBody();
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public List<LoanResponse> getMyLoans (){
        try {
            return Optional.ofNullable(client.getMyLoans().getBody())
                    .orElse(List.of());
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public List<AccountResponse> getAvailableCreditAccounts (){
        try {
            return Optional.ofNullable(client.getAvailableCreditAccounts().getBody())
                    .orElse(List.of());
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }
}