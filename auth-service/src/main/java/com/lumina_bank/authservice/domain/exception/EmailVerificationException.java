package com.lumina_bank.authservice.domain.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class EmailVerificationException extends BusinessException {
    public EmailVerificationException(String message) {
        super(message, HttpStatus.BAD_REQUEST.value());
    }
}
