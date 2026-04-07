package com.fincore.transaction.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transfer {

    private final UUID id;
    private final UUID sourceAccountId;
    private final UUID targetAccountId;
    private final BigDecimal amount;
    private final Currency currency;
    private TransferStatus status;
    private String failureReason;
    private final String idempotencyKey;
    private final LocalDateTime createdAt;

    public enum Currency { PEN, USD }

    public enum TransferStatus {
        PENDING, COMPLETED, FAILED, CANCELLED
    }

    // Constructor for creating a new transfer
    public Transfer(UUID sourceAccountId, UUID targetAccountId,
                    BigDecimal amount, Currency currency,
                    String idempotencyKey) {
        this.id               = UUID.randomUUID();
        this.sourceAccountId  = sourceAccountId;
        this.targetAccountId  = targetAccountId;
        this.amount           = amount;
        this.currency         = currency;
        this.status           = TransferStatus.PENDING;
        this.idempotencyKey   = idempotencyKey;
        this.createdAt        = LocalDateTime.now();
    }

    // Constructor for rebuilding from database
    public Transfer(UUID id, UUID sourceAccountId, UUID targetAccountId,
                    BigDecimal amount, Currency currency,
                    TransferStatus status, String failureReason,
                    String idempotencyKey, LocalDateTime createdAt) {
        this.id               = id;
        this.sourceAccountId  = sourceAccountId;
        this.targetAccountId  = targetAccountId;
        this.amount           = amount;
        this.currency         = currency;
        this.status           = status;
        this.failureReason    = failureReason;
        this.idempotencyKey   = idempotencyKey;
        this.createdAt        = createdAt;
    }

    public void complete() {
        this.status = TransferStatus.COMPLETED;
    }

    public void fail(String reason) {
        this.status        = TransferStatus.FAILED;
        this.failureReason = reason;
    }

    public void cancel(String reason) {
        this.status        = TransferStatus.CANCELLED;
        this.failureReason = reason;
    }

    public UUID getId()                  { return id; }
    public UUID getSourceAccountId()     { return sourceAccountId; }
    public UUID getTargetAccountId()     { return targetAccountId; }
    public BigDecimal getAmount()        { return amount; }
    public Currency getCurrency()        { return currency; }
    public TransferStatus getStatus()    { return status; }
    public String getFailureReason()     { return failureReason; }
    public String getIdempotencyKey()    { return idempotencyKey; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
}