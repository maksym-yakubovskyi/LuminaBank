package com.lumina_bank.paymentservice.infrastructure.external.transaction.dto;

public record TransactionResponse(
        Long outcomingTransactionId,
        Long incomingTransactionId
) { }