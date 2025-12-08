package com.lumina_bank.paymentservice.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidPaymentRequestException extends BusinessException {
    public InvalidPaymentRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
