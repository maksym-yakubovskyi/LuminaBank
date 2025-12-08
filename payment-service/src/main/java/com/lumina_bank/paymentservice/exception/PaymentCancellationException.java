package com.lumina_bank.paymentservice.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class PaymentCancellationException extends BusinessException {
    public PaymentCancellationException(String message) {
        super(message,HttpStatus.BAD_REQUEST);
    }
}

