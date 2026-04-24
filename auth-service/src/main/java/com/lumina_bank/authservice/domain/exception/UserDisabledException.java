package com.lumina_bank.authservice.domain.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UserDisabledException extends BusinessException {
    public UserDisabledException(String message) {
        super(message, HttpStatus.FORBIDDEN.value());
    }
}
