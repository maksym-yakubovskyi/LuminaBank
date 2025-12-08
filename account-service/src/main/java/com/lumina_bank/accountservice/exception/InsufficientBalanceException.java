package com.lumina_bank.accountservice.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InsufficientBalanceException extends BusinessException {
    public InsufficientBalanceException(String message) {
        super(message, HttpStatus.PAYMENT_REQUIRED);
    }
}
