package com.lumina_bank.aiassistantservice.infrastructure.external.account;

import com.lumina_bank.aiassistantservice.infrastructure.external.account.dto.*;
import com.lumina_bank.aiassistantservice.infrastructure.external.FeignExceptionMapper;
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
            return Optional.ofNullable(client.getUserAccounts())
                    .orElse(List.of());
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public AccountResponse createAccount(AccountCreateRequest dto){
        try {
            return client.createAccount(dto);
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }


    public List<CardResponse> getMyCards(){
        try {
            return Optional.ofNullable(client.getMyCards())
                    .orElse(List.of());
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public CardResponse createCard (Long accountId, CardCreateRequest dto){
        try {
            return client.createCard(accountId,dto);
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public List<CardResponse> getCardsByAccountId(Long accountId){
        try {
            return Optional.ofNullable(client.getCardsByAccountId(accountId))
                    .orElse(List.of());
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public List<LoanOfferResponse> getLoanOffers (LoanApplicationRequest request){
        try {
            return Optional.ofNullable(client.getLoanOffers(request))
                    .orElse(List.of());
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public LoanResponse approveLoan (LoanApplicationRequest request){
        try {
            return client.approveLoan(request);
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public List<LoanResponse> getMyLoans (){
        try {
            return Optional.ofNullable(client.getMyLoans())
                    .orElse(List.of());
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public List<AccountResponse> getAvailableCreditAccounts (){
        try {
            return Optional.ofNullable(client.getAvailableCreditAccounts())
                    .orElse(List.of());
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }
}