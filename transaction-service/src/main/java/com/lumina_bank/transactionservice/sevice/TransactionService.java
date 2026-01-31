package com.lumina_bank.transactionservice.sevice;

import com.lumina_bank.common.enums.payment.PaymentDirection;
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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountClientService accountClientService;

    public List<Transaction> makeTransaction(TransactionCreateDto request) {
        if (request.fromCardNumber().equals(request.toCardNumber()))
            throw new SameAccountTransactionException("Sender and receiver cards cannot be the same");

        UUID transferId = UUID.randomUUID();

        Transaction out = createOutgoing(request, transferId);
        Transaction in = createIncoming(request, transferId);

        log.debug("Created transaction with id={},{}, status={},{}",
                out.getId(),in.getId(),out.getTransactionStatus(),in.getTransactionStatus());

        try{
            performExternalTransfer(request);

            out.setTransactionStatus(TransactionStatus.SUCCESS);
            in.setTransactionStatus(TransactionStatus.SUCCESS);

            log.info("Transaction successfully: id={},{}, status={},{}",
                    out.getId(),in.getId(),out.getTransactionStatus(),in.getTransactionStatus());
        }catch (BusinessException e){
            out.setTransactionStatus(TransactionStatus.FAILURE);
            in.setTransactionStatus(TransactionStatus.FAILURE);

            log.warn("Business error during transaction id={},{}: {}",out.getId(),in.getId(),e.getMessage());
            throw e;
        }catch(Exception e){
            out.setTransactionStatus(TransactionStatus.FAILURE);
            in.setTransactionStatus(TransactionStatus.FAILURE);

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

    private Transaction createOutgoing(TransactionCreateDto request, UUID transferId) {
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

    private Transaction createIncoming(TransactionCreateDto request, UUID transferId) {
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

    private void performExternalTransfer(TransactionCreateDto request) {
        log.debug("Perform transaction: withdraw  amount={}",  request.amount());

        callExternalTransaction(
                TransactionOperation.WITHDRAW,
                request.fromCardNumber(),
                request.amount());

        try{
            log.debug("Depositing converted amount={}", request.amount());

            callExternalTransaction(
                    TransactionOperation.DEPOSIT,
                    request.toCardNumber(),
                    request.convertedAmount());

        }catch(Exception e){
            callExternalTransaction(
                    TransactionOperation.DEPOSIT,
                    request.fromCardNumber(),
                    request.amount());

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