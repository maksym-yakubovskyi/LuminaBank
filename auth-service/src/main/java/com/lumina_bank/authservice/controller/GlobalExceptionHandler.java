package com.lumina_bank.authservice.controller;

import com.lumina_bank.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static com.lumina_bank.common.exception.ErrorResponse.buildErrorResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Валідація DTO Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msq = ex.getBindingResult().getFieldErrors()
                .stream().map(e -> e.getField() + ":" + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation Exception: {}", msq);

        return ResponseEntity.badRequest().body(buildErrorResponse(HttpStatus.BAD_REQUEST, msq, req.getRequestURI()));
    }

    // всі внутрішні помилки
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException ex, HttpServletRequest req) {
        log.warn("BusinessException : {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatus())
                .body(buildErrorResponse(ex.getStatus(), ex.getMessage(), req.getRequestURI()));
    }

    //Інші помилки
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOtherException(Exception ex, HttpServletRequest req) {
        log.warn("Unexpected exception at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req.getRequestURI()));
    }
}