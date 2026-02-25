package com.lumina_bank.aiassistantservice.domain.exception;

import com.lumina_bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ConversationNotFoundException extends BusinessException {
    public ConversationNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND.value());
    }
}
