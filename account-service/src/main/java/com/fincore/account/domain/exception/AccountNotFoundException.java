package com.fincore.account.domain.exception;

public class AccountNotFoundException extends DomainException {

    public AccountNotFoundException(String accountId) {
        super("ACCOUNT_NOT_FOUND",
                "Account not found: " + accountId);
    }
}