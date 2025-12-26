package com.lumina_bank.authservice.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class EmailVerificationNotFoundException extends BusinessException {
    public EmailVerificationNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
