package com.fincore.account.domain.usecase;

import com.fincore.account.domain.model.Account;
import com.fincore.account.domain.model.Account.AccountType;
import com.fincore.account.domain.model.Account.Currency;
import com.fincore.account.domain.port.AccountNumberGenerator;

import java.util.UUID;

public class CreateAccountUseCase {

    private final AccountNumberGenerator accountNumberGenerator;

    public CreateAccountUseCase(AccountNumberGenerator accountNumberGenerator) {
        this.accountNumberGenerator = accountNumberGenerator;
    }

    public Account execute(UUID userId, AccountType type, Currency currency) {
        return new Account(
                accountNumberGenerator.generate(),
                userId,
                type,
                currency
        );
    }
}