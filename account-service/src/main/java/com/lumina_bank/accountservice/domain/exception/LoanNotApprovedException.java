package com.lumina_bank.accountservice.domain.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class LoanNotApprovedException extends BusinessException {

    public LoanNotApprovedException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY.value());
    }
}
