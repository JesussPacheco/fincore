package com.fincore.account.domain.exception;

public class UnauthorizedAccountAccessException extends DomainException {

    public UnauthorizedAccountAccessException() {
        super("UNAUTHORIZED_ACCOUNT_ACCESS",
                "You do not have access to this account");
    }
}