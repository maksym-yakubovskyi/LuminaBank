package com.lumina_bank.transactionservice.api.response;

public record TransactionResponse(
        Long outcomingTransactionId,
        Long incomingTransactionId
) { }