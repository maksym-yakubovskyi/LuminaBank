package com.lumina_bank.transactionservice.sevice;

import com.lumina_bank.common.exception.BusinessException;
import com.lumina_bank.transactionservice.dto.client.AccountOperationDto;
import com.lumina_bank.transactionservice.dto.client.AccountResponse;
import com.lumina_bank.transactionservice.dto.TransactionCreateDto;
import com.lumina_bank.transactionservice.enums.TransactionOperation;
import com.lumina_bank.transactionservice.enums.TransactionStatus;
import com.lumina_bank.transactionservice.exception.*;
import com.lumina_bank.transactionservice.model.Transaction;
import com.lumina_bank.transactionservice.repository.TransactionRepository;
import com.lumina_bank.transactionservice.sevice.client.AccountClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountClientService accountClientService;

    public Transaction makeTransaction(TransactionCreateDto request) {
        if (request.fromCardNumber().equals(request.toCardNumber()))
            throw new SameAccountTransactionException("Sender and receiver cards cannot be the same");

        Transaction transaction = createPendingTransaction(request);

        log.debug("Created transaction with id={}, status={}", transaction.getId(),transaction.getTransactionStatus());

        try{
            performTransaction(request);
            transaction.setTransactionStatus(TransactionStatus.SUCCESS);
            log.info("Transaction successfully: id={}, status={}", transaction.getId(),transaction.getTransactionStatus());
        }catch (BusinessException e){
            transaction.setTransactionStatus(TransactionStatus.FAILURE);
            log.warn("Business error during transaction id={}: {}",transaction.getId(),e.getMessage());
            throw e;
        }catch(Exception e){
            transaction.setTransactionStatus(TransactionStatus.FAILURE);
            log.error("Unexpected error during transaction id={}", transaction.getId(), e);
            throw new ExternalServiceException("Transaction failed due to external service error", e);
        }finally {
            transaction.setFromCurrency(request.fromCurrency());
            transaction.setToCurrency(request.toCurrency());
            transaction.setConvertedAmount(request.convertedAmount());
            transaction.setExchangeRate(request.exchangeRate());
            transactionRepository.save(transaction);
            log.debug("Transaction saved to DB: id={}, status={}", transaction.getId(),transaction.getTransactionStatus());
        }
        return transaction;
    }

    private Transaction createPendingTransaction(TransactionCreateDto request) {
        Transaction transaction = Transaction.builder()
                .fromCardNumber(request.fromCardNumber())
                .toCardNumber(request.toCardNumber())
                .amount(request.amount())
                .transactionStatus(TransactionStatus.PENDING)
                .description(request.description())
                .build();
        return transactionRepository.save(transaction);
    }

    private void performTransaction(TransactionCreateDto request) {
        log.debug("Perform transaction: withdraw  amount={}",  request.amount());
        callExternalTransaction(TransactionOperation.WITHDRAW, request.fromCardNumber(),request.amount());

        try{
            log.debug("Depositing converted amount={}", request.amount());
            callExternalTransaction(TransactionOperation.DEPOSIT, request.toCardNumber(),request.convertedAmount());
        }catch(Exception e){
            callExternalTransaction(TransactionOperation.DEPOSIT, request.fromCardNumber(),request.amount());
            log.warn("Deposit failed with error={}" , e.getMessage());
            throw new ExternalServiceException("Deposit failed, transaction rolled back", e);
        }
    }

    private void callExternalTransaction(TransactionOperation operation, String cardNumber, BigDecimal amount) {
        log.debug("Calling external service for {} operation", operation);

        ResponseEntity<AccountResponse> response;
        AccountOperationDto dto = AccountOperationDto.builder().amount(amount).cardNumber(cardNumber).build();

        switch (operation) {
            case WITHDRAW -> response = accountClientService.withdraw(dto);
            case DEPOSIT -> response = accountClientService.deposit(dto);
            default -> throw new UnknownOperationException("Unknown operation: " + operation);
        }
        if (response == null) {
            log.warn("{} operation returned null response", operation);
            throw new ExternalServiceException("Null response from account service");
        }
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.warn("{} operation failed: status={}, body={}", operation, response.getStatusCode(), response.getBody());
            throw new ExternalServiceException(
                    String.format("%s request failed: status=%s, body=%s",
                    operation, response.getStatusCode(), response.getBody()));
        }
    }
}