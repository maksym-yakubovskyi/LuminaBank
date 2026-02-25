package com.lumina_bank.transactionservice.application.service;

import com.lumina_bank.common.exception.BusinessException;
import com.lumina_bank.common.exception.ExternalServiceException;
import com.lumina_bank.transactionservice.domain.enums.PaymentDirection;
import com.lumina_bank.transactionservice.domain.exception.SameAccountTransactionException;
import com.lumina_bank.transactionservice.domain.exception.UnknownOperationException;
import com.lumina_bank.transactionservice.infrastructure.external.account.dto.AccountOperationRequest;
import com.lumina_bank.transactionservice.api.request.TransactionCreateRequest;
import com.lumina_bank.transactionservice.domain.enums.TransactionOperation;
import com.lumina_bank.transactionservice.domain.enums.TransactionStatus;
import com.lumina_bank.transactionservice.domain.model.Transaction;
import com.lumina_bank.transactionservice.domain.repository.TransactionRepository;
import com.lumina_bank.transactionservice.infrastructure.external.account.AccountClientService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountClientService accountClientService;

    public List<Transaction> makeTransaction(TransactionCreateRequest request) {
        if (request.fromCardNumber().equals(request.toCardNumber()))
            throw new SameAccountTransactionException("Sender and receiver cards cannot be the same");

        UUID transferId = UUID.randomUUID();

        Transaction out = createOutgoing(request, transferId);
        Transaction in = createIncoming(request, transferId);

        transactionRepository.saveAll(List.of(out, in));

        log.debug("Created transaction with id={},{}, status={},{}",
                out.getId(),in.getId(),out.getTransactionStatus(),in.getTransactionStatus());

        try{
            performExternalTransfer(request);

            out.setTransactionStatus(TransactionStatus.SUCCESS);
            in.setTransactionStatus(TransactionStatus.SUCCESS);

            log.info("Transaction successfully: id={},{}, status={},{}",
                    out.getId(),in.getId(),out.getTransactionStatus(),in.getTransactionStatus());
        }catch (BusinessException e){
            markAsFailure(out, in);

            log.warn("Business error during transaction id={},{}: {}",out.getId(),in.getId(),e.getMessage());
            throw e;
        }catch(Exception e){
            markAsFailure(out, in);

            log.warn("Unexpected error during transaction id={},{}: {}",out.getId(),in.getId(),e.getMessage());
            throw new ExternalServiceException("Transaction failed due to external service error", e);
        }finally {

            transactionRepository.save(out);
            transactionRepository.save(in);

            log.debug("Transaction saved to DB: id={},{}, status={},{}",
                    out.getId(),in.getId(),out.getTransactionStatus(),in.getTransactionStatus());
        }

        return List.of(out, in);
    }

    private Transaction createOutgoing(TransactionCreateRequest request, UUID transferId) {
        return Transaction.builder()
                .transferId(transferId)
                .userId(request.userId())
                .cardNumber(request.fromCardNumber())
                .currency(request.fromCurrency())
                .amount(request.amount())
                .exchangeRate(request.exchangeRate())
                .direction(PaymentDirection.OUTGOING)
                .transactionStatus(TransactionStatus.PENDING)
                .category(request.category())
                .description(request.description())
                .build();
    }

    private Transaction createIncoming(TransactionCreateRequest request, UUID transferId) {
        return Transaction.builder()
                .transferId(transferId)
                .userId(request.toAccountOwnerId())
                .cardNumber(request.toCardNumber())
                .currency(request.toCurrency())
                .amount(request.convertedAmount())
                .exchangeRate(request.exchangeRate())
                .direction(PaymentDirection.INCOMING)
                .transactionStatus(TransactionStatus.PENDING)
                .category(request.category())
                .description(request.description())
                .build();
    }

    private void performExternalTransfer(TransactionCreateRequest request) {
        log.debug("Perform transaction: withdraw  amount={}",  request.amount());

        callExternalTransaction(
                TransactionOperation.WITHDRAW,
                request.fromCardNumber(),
                request.amount());

        try{
            log.debug("Depositing converted amount={}", request.convertedAmount());

            callExternalTransaction(
                    TransactionOperation.DEPOSIT,
                    request.toCardNumber(),
                    request.convertedAmount());

        }catch(Exception e){
            try {
                callExternalTransaction(
                        TransactionOperation.DEPOSIT,
                        request.fromCardNumber(),
                        request.amount());
            } catch (Exception rollbackEx) {
                log.error("Rollback failed! Manual intervention required. {}", rollbackEx.getMessage());
            }

            throw new ExternalServiceException("Deposit failed, transaction rolled back", e);
        }
    }

    private void callExternalTransaction(TransactionOperation operation, String cardNumber, BigDecimal amount) {
        log.debug("Calling external service for {} operation", operation);

        AccountOperationRequest request = AccountOperationRequest.builder()
                .amount(amount)
                .cardNumber(cardNumber)
                .build();

        try{
            switch (operation) {
                case WITHDRAW -> accountClientService.withdraw(request);
                case DEPOSIT -> accountClientService.deposit(request);
                default -> throw new UnknownOperationException("Unknown operation: " + operation);
            }
            log.debug("Account-service {} successful for card={}", operation, cardNumber);

        }catch (FeignException e) {
            log.warn("Account-service {} failed. status={}, response={}", operation, e.status(), e.contentUTF8());
            throw new ExternalServiceException(String.format("Account-service %s failed with status %s", operation, e.status()), e);
        } catch (Exception e) {
            log.error("Unexpected error during {} operation: {}", operation, e.getMessage());
            throw new ExternalServiceException("Unexpected error while calling account-service", e);
        }
    }

    private void markAsFailure(Transaction out, Transaction in) {
        out.setTransactionStatus(TransactionStatus.FAILURE);
        in.setTransactionStatus(TransactionStatus.FAILURE);
    }
}