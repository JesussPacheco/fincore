package com.fincore.account.domain.exception;

public class InsufficientFundsException extends DomainException {

    public InsufficientFundsException() {
        super("INSUFFICIENT_FUNDS", "Account does not have sufficient funds");
    }
}