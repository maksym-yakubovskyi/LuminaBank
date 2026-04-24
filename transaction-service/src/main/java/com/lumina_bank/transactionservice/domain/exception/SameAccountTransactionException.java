package com.lumina_bank.transactionservice.domain.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class SameAccountTransactionException extends BusinessException
{
    public SameAccountTransactionException(String message) {
        super(message, HttpStatus.BAD_REQUEST.value());
    }
}
