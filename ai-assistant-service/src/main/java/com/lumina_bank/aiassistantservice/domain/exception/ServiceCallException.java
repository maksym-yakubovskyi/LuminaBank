package com.lumina_bank.aiassistantservice.domain.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ServiceCallException extends BusinessException {

    public ServiceCallException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
