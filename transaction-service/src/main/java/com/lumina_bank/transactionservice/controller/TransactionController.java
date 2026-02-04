package com.lumina_bank.transactionservice.controller;

import com.lumina_bank.transactionservice.dto.TransactionCreateDto;
import com.lumina_bank.transactionservice.dto.TransactionResponse;
import com.lumina_bank.transactionservice.model.Transaction;
import com.lumina_bank.transactionservice.sevice.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<?> makeTransaction(@Valid @RequestBody TransactionCreateDto request) {
        log.info("POST /transactions/transfer - Making Transaction");

        List<Transaction> transactions = transactionService.makeTransaction(request);
        Long outTransactionId = transactions.get(0).getId();
        Long inTransactionId = transactions.get(1).getId();

        log.info("Transaction created id = {},{}",outTransactionId,inTransactionId);

        return ResponseEntity.created(URI.create("/transactions/")).body(new TransactionResponse(outTransactionId, inTransactionId));

    }
}
