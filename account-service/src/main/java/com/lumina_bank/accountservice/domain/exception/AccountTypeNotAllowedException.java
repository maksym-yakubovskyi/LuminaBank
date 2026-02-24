package com.lumina_bank.accountservice.domain.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class AccountTypeNotAllowedException extends BusinessException {
    public AccountTypeNotAllowedException(String message) {
        super(message, HttpStatus.FORBIDDEN.value());
    }
}
