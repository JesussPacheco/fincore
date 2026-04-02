package com.fincore.account.infrastructure.persistence;

import com.fincore.account.domain.model.Account;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class AccountJpaEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Account.AccountType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Account.Currency currency;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Account.AccountStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    protected AccountJpaEntity() {}

    public static AccountJpaEntity fromDomain(Account account) {
        AccountJpaEntity entity = new AccountJpaEntity();
        entity.id            = account.getId();
        entity.accountNumber = account.getAccountNumber();
        entity.userId        = account.getUserId();
        entity.type          = account.getType();
        entity.currency      = account.getCurrency();
        entity.balance       = account.getBalance();
        entity.status        = account.getStatus();
        entity.createdAt     = account.getCreatedAt();
        entity.updatedAt     = LocalDateTime.now();
        return entity;
    }

    public Account toDomain() {
        return new Account(id, accountNumber, userId, type,
                currency, balance, status, createdAt);
    }
}