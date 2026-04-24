package com.lumina_bank.paymentservice.domain.enums;

public enum PaymentStatus {
    PENDING,     // створено, очікує виконання
    PROCESSING,  // виконується
    SUCCESS,     // успішно виконано
    FAILED,      // помилка
    CANCELLED,    // скасовано
    FLAGGED,        // підозрілий
    BLOCKED,         // заблокований
    REJECTED        // відхилена менеджером
}