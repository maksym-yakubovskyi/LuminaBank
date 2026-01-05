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

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<?> makeTransaction(@Valid @RequestBody TransactionCreateDto request) {
        log.info("POST /transactions/transfer - Making Transaction");

        Transaction transaction = transactionService.makeTransaction(request);

        log.info("Transaction created id = {}",transaction.getId());

        return ResponseEntity.created(URI.create("/transactions/" + transaction.getId()))
                .body(TransactionResponse.fromEntity(transaction, transaction.getFromAccountId()));

    }

//    @GetMapping("/{accountId}")
//    public ResponseEntity<?> getTransactions(@PathVariable Long accountId) {
//        log.info("GET /transactions/{accountId} - Fetching transactions with accountId={}", accountId);
//
//        return ResponseEntity.ok(transactionService.getAllUsersTransactions(accountId));
//    }

}
