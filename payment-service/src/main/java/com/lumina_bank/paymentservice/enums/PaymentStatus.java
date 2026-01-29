package com.lumina_bank.paymentservice.enums;

public enum PaymentStatus {
    RISK_PENDING,
    PENDING,     // створено, очікує виконання
    PROCESSING,  // виконується
    SUCCESS,     // успішно виконано
    FAILED,      // помилка
    CANCELLED,    // скасовано
    FLAGGED,        // підозрілий
    BLOCKED,         // заблокований
    REJECTED        // відхилена менеджером
}