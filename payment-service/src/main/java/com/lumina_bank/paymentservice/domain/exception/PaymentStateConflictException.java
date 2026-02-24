package com.lumina_bank.paymentservice.domain.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class PaymentStateConflictException extends BusinessException {
    public PaymentStateConflictException(String message) {
        super(message, HttpStatus.CONFLICT.value());
    }
}
