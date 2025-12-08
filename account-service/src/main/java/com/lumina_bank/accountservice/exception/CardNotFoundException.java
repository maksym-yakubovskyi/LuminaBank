package com.lumina_bank.accountservice.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class CardNotFoundException extends BusinessException {
    public CardNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
