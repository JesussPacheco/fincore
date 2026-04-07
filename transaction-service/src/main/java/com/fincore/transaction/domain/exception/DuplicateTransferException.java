package com.fincore.transaction.domain.exception;

public class DuplicateTransferException extends DomainException {

    public DuplicateTransferException(String idempotencyKey) {
        super("DUPLICATE_TRANSFER",
                "Transfer already processed with key: " + idempotencyKey);
    }
}