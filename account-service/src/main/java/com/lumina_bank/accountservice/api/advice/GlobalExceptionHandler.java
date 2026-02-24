package com.lumina_bank.accountservice.api.advice;

import com.lumina_bank.common.exception.BusinessException;
import com.lumina_bank.common.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Валідація DTO Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream().map(e -> e.getField() + ":" + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation exception at {}: {}", req.getRequestURI(), message);

        return ResponseEntity.badRequest().body(
                ErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        message,
                        req.getRequestURI()
                )
        );
    }

    // всі внутрішні помилки
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatus());

        log.warn("Business exception at {}: {}", req.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(status).body(
                ErrorResponse.of(
                        status.value(),
                        status.getReasonPhrase(),
                        ex.getMessage(),
                        req.getRequestURI()
                )
        );
    }

    //Інші помилки
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOtherException(Exception ex, HttpServletRequest req) {
        log.error("Unexpected error at {}", req.getRequestURI(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse.of(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        ex.getMessage(),
                        req.getRequestURI()
                )
        );
    }
}

