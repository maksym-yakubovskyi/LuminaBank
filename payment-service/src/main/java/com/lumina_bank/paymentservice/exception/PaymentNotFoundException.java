package com.lumina_bank.paymentservice.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class PaymentNotFoundException extends BusinessException {
    public PaymentNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
