package com.fincore.account.infrastructure.web;

import com.fincore.account.domain.model.Account.AccountType;
import com.fincore.account.domain.model.Account.Currency;
import jakarta.validation.constraints.NotNull;

public record CreateAccountRequest(

        @NotNull(message = "Account type is required")
        AccountType type,

        @NotNull(message = "Currency is required")
        Currency currency
) {}