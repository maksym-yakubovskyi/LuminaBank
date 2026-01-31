package com.lumina_bank.transactionservice.dto;

public record TransactionResponse(
        Long outcomingTransactionId,
        Long incomingTransactionId
) { }