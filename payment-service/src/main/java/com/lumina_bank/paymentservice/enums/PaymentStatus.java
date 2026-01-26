package com.lumina_bank.paymentservice.enums;

public enum PaymentStatus {
    PENDING,     // створено, очікує виконання
    PROCESSING,  // виконується
    SUCCESS,     // успішно виконано
    FAILED,      // помилка
    CANCELLED    // скасовано
}