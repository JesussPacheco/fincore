package com.fincore.transaction.domain.exception;

public class TransferLockException extends DomainException {

    public TransferLockException(String accountId) {
        super("TRANSFER_LOCK_UNAVAILABLE",
                "Account has a transfer in progress: " + accountId);
    }
}