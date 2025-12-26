package com.lumina_bank.authservice.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UserLockedException extends BusinessException {
    public UserLockedException(String message) {
        super(message, HttpStatus.LOCKED);
    }
}
