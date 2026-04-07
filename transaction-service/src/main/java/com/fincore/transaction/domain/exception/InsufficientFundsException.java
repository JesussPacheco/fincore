package com.fincore.transaction.domain.exception;

public class InsufficientFundsException extends DomainException {

    public InsufficientFundsException() {
        super("INSUFFICIENT_FUNDS", "Source account does not have sufficient funds");
    }
}