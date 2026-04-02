package com.fincore.account.infrastructure.web;

import com.fincore.account.domain.model.Account;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String accountNumber,
        String type,
        String currency,
        BigDecimal balance,
        String status,
        LocalDateTime createdAt
) {
    public static AccountResponse fromDomain(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getType().name(),
                account.getCurrency().name(),
                account.getBalance(),
                account.getStatus().name(),
                account.getCreatedAt()
        );
    }
}