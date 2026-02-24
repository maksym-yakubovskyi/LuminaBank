package com.lumina_bank.paymentservice.domain.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class PaymentTemplateAccessDeniedException extends BusinessException {
    public PaymentTemplateAccessDeniedException(String message) {
        super(message, HttpStatus.BAD_REQUEST.value());
    }
}
