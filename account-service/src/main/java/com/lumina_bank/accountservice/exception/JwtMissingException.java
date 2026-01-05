package com.lumina_bank.accountservice.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class JwtMissingException extends BusinessException {
    public JwtMissingException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
