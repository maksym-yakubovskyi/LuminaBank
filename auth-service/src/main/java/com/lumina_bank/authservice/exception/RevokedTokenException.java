package com.lumina_bank.authservice.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class RevokedTokenException extends BusinessException {
    public RevokedTokenException (String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
