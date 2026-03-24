package com.fincore.auth.domain.exception;

public class EmailAlreadyExistsException extends DomainException {

    public EmailAlreadyExistsException(String email) {
        super("EMAIL_ALREADY_EXISTS",
                "Email already registered: " + email);
    }
}