package com.lumina_bank.accountservice.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidAmountException extends BusinessException
{
    public InvalidAmountException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
