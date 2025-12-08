package com.lumina_bank.accountservice.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class AccountLockedException extends BusinessException {
    public AccountLockedException(String message) {
        super(message, HttpStatus.LOCKED);
    }
}
