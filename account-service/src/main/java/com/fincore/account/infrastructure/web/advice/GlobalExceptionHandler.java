package com.fincore.account.infrastructure.web.advice;

import com.fincore.account.domain.exception.AccountNotFoundException;
import com.fincore.account.domain.exception.DomainException;
import com.fincore.account.domain.exception.InsufficientFundsException;
import com.fincore.account.domain.exception.UnauthorizedAccountAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");
        return ResponseEntity.badRequest().body(errorBody(
                HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message
        ));
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            AccountNotFoundException ex) {
        log.warn("Account not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody(
                HttpStatus.NOT_FOUND, ex.getErrorCode(), ex.getMessage()
        ));
    }

    @ExceptionHandler(UnauthorizedAccountAccessException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(
            UnauthorizedAccountAccessException ex) {
        log.warn("Unauthorized access: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorBody(
                HttpStatus.FORBIDDEN, ex.getErrorCode(), ex.getMessage()
        ));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientFunds(
            InsufficientFundsException ex) {
        log.warn("Insufficient funds: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorBody(
                HttpStatus.UNPROCESSABLE_ENTITY, ex.getErrorCode(), ex.getMessage()
        ));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomain(DomainException ex) {
        log.error("Domain exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorBody(
                HttpStatus.UNPROCESSABLE_ENTITY, ex.getErrorCode(), ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody(
                HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred"
        ));
    }

    private Map<String, Object> errorBody(HttpStatus status, String code, String message) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    status.value(),
                "error",     code,
                "message",   message
        );
    }
}