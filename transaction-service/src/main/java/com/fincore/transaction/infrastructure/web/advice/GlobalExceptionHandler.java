package com.fincore.transaction.infrastructure.web.advice;

import com.fincore.transaction.domain.exception.DomainException;
import com.fincore.transaction.domain.exception.DuplicateTransferException;
import com.fincore.transaction.domain.exception.InsufficientFundsException;
import com.fincore.transaction.domain.exception.TransferLockException;
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

    @ExceptionHandler(DuplicateTransferException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(
            DuplicateTransferException ex) {
        log.warn("Duplicate transfer: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody(
                HttpStatus.CONFLICT, ex.getErrorCode(), ex.getMessage()
        ));
    }

    @ExceptionHandler(TransferLockException.class)
    public ResponseEntity<Map<String, Object>> handleLock(
            TransferLockException ex) {
        log.warn("Transfer lock unavailable: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody(
                HttpStatus.CONFLICT, ex.getErrorCode(), ex.getMessage()
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