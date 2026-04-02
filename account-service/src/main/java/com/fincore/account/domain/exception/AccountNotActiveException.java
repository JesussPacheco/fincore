package com.fincore.account.domain.exception;

public class AccountNotActiveException extends DomainException {

    public AccountNotActiveException(String accountNumber) {
        super("ACCOUNT_NOT_ACTIVE",
                "Account is not active: " + accountNumber);
    }
}