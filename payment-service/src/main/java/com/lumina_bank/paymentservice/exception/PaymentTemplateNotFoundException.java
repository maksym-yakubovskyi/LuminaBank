package com.lumina_bank.paymentservice.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class PaymentTemplateNotFoundException extends BusinessException {
    public PaymentTemplateNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
