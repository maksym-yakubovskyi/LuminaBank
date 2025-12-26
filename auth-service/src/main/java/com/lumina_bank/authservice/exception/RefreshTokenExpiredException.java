package com.lumina_bank.authservice.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class RefreshTokenExpiredException extends BusinessException {
    public RefreshTokenExpiredException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
