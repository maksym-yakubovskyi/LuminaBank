package com.lumina_bank.transactionservice.sevice;

import com.lumina_bank.common.exception.BusinessException;
import com.lumina_bank.transactionservice.dto.client.AccountOperationDto;
import com.lumina_bank.transactionservice.dto.client.AccountResponse;
import com.lumina_bank.transactionservice.dto.TransactionCreateDto;
//import com.lumina_bank.transactionservice.dto.TransactionResponse;
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
//import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
//import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountClientService accountClientService;

    public Transaction makeTransaction(TransactionCreateDto request) {
        if (request.fromAccountId().equals(request.toAccountId()))
            throw new SameAccountTransactionException("Sender and receiver accounts cannot be the same");

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

//    @Transactional(readOnly = true)
//    public List<TransactionResponse> getAllUsersTransactions(Long accountId) {
//        return transactionRepository.findAllByFromAccountIdOrToAccountId(accountId, accountId)
//                .stream()
//                .map(t -> TransactionResponse.fromEntity(t, accountId))
//                .toList();
//    }

    private Transaction createPendingTransaction(TransactionCreateDto request) {
        Transaction transaction = Transaction.builder()
                .fromAccountId(request.fromAccountId())
                .toAccountId(request.toAccountId())
                .amount(request.amount())
                .transactionStatus(TransactionStatus.PENDING)
                .description(request.description())
                .build();
        return transactionRepository.save(transaction);
    }

    private void performTransaction(TransactionCreateDto request) {
        log.debug("Perform transaction: withdraw from accountId={}, amount={}", request.fromAccountId(), request.amount());
        callExternalTransaction(TransactionOperation.WITHDRAW, request.fromAccountId(),request.amount());

        try{
            log.debug("Depositing converted amount={}, to accountId={}", request.amount(), request.toAccountId());
            callExternalTransaction(TransactionOperation.DEPOSIT, request.toAccountId(),request.convertedAmount());
        }catch(Exception e){
            callExternalTransaction(TransactionOperation.DEPOSIT, request.fromAccountId(),request.amount());
            log.warn("Deposit failed for accountId={}, with error={}",request.toAccountId(), e.getMessage());
            throw new ExternalServiceException("Deposit failed, transaction rolled back", e);
        }
    }

    private void callExternalTransaction(TransactionOperation operation, Long accountId, BigDecimal amount) {
        log.debug("Calling external service for {} operation: accountId={}, amount={}", operation, accountId, amount);

        ResponseEntity<AccountResponse> response;
        AccountOperationDto dto = AccountOperationDto.builder().amount(amount).build();

        switch (operation) {
            case WITHDRAW -> response = accountClientService.withdraw(accountId, dto);
            case DEPOSIT -> response = accountClientService.deposit(accountId, dto);
            default -> throw new UnknownOperationException("Unknown operation: " + operation);
        }
        if (response == null) {
            log.warn("{} operation returned null response for accountId={}", operation, accountId);
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
