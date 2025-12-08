package com.lumina_bank.transactionservice.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UnknownOperationException extends BusinessException {
    public UnknownOperationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
