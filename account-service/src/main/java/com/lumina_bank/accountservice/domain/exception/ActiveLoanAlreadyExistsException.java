package com.lumina_bank.accountservice.domain.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ActiveLoanAlreadyExistsException extends BusinessException {

    public ActiveLoanAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT.value());
    }
}
