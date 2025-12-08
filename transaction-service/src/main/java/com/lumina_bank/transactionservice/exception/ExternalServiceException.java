package com.lumina_bank.transactionservice.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ExternalServiceException extends BusinessException {
    public ExternalServiceException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
