package com.fincore.account.domain.model;

import com.fincore.account.domain.exception.AccountNotActiveException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Account {

    private final UUID id;
    private final String accountNumber;
    private final UUID userId;
    private final AccountType type;
    private final Currency currency;
    private BigDecimal balance;
    private AccountStatus status;
    private final LocalDateTime createdAt;

    public enum AccountType { SAVINGS, CHECKING }
    public enum Currency    { PEN, USD }
    public enum AccountStatus { ACTIVE, INACTIVE, BLOCKED }

    // Constructor for creating a new account
    public Account(String accountNumber, UUID userId,
                   AccountType type, Currency currency) {
        this.id            = UUID.randomUUID();
        this.accountNumber = accountNumber;
        this.userId        = userId;
        this.type          = type;
        this.currency      = currency;
        this.balance       = BigDecimal.ZERO;
        this.status        = AccountStatus.ACTIVE;
        this.createdAt     = LocalDateTime.now();
    }

    // Constructor for rebuilding from database
    public Account(UUID id, String accountNumber, UUID userId,
                   AccountType type, Currency currency,
                   BigDecimal balance, AccountStatus status,
                   LocalDateTime createdAt) {
        this.id            = id;
        this.accountNumber = accountNumber;
        this.userId        = userId;
        this.type          = type;
        this.currency      = currency;
        this.balance       = balance;
        this.status        = status;
        this.createdAt     = createdAt;
    }

    // Business logic — debit account
    public void debit(BigDecimal amount) {
        validateActive();
        validateSufficientFunds(amount);
        this.balance = this.balance.subtract(amount);
    }

    // Business logic — credit account
    public void credit(BigDecimal amount) {
        validateActive();
        this.balance = this.balance.add(amount);
    }

    public boolean isOwnedBy(UUID userId) {
        return this.userId.equals(userId);
    }
    private void validateActive() {

        if (this.status != AccountStatus.ACTIVE)
            throw new AccountNotActiveException(this.accountNumber);
    }

    private void validateSufficientFunds(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0)
            throw new IllegalStateException("Insufficient funds");
    }

    public UUID getId()                { return id; }
    public String getAccountNumber()   { return accountNumber; }
    public UUID getUserId()            { return userId; }
    public AccountType getType()       { return type; }
    public Currency getCurrency()      { return currency; }
    public BigDecimal getBalance()     { return balance; }
    public AccountStatus getStatus()   { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}