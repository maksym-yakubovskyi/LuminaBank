package com.lumina_bank.authservice.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class TokenNotFoundException extends BusinessException {
    public TokenNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
