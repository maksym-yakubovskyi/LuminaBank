package com.lumina_bank.paymentservice.dto.client;

public record TransactionResponse(
        Long outcomingTransactionId,
        Long incomingTransactionId
) { }