package com.fincore.transaction.infrastructure.persistence;

import com.fincore.transaction.domain.model.Transfer;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transfers")
public class TransferJpaEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "source_account_id", nullable = false)
    private UUID sourceAccountId;

    @Column(name = "target_account_id", nullable = false)
    private UUID targetAccountId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Transfer.Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Transfer.TransferStatus status;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    protected TransferJpaEntity() {}

    public static TransferJpaEntity fromDomain(Transfer transfer) {
        TransferJpaEntity entity = new TransferJpaEntity();
        entity.id              = transfer.getId();
        entity.sourceAccountId = transfer.getSourceAccountId();
        entity.targetAccountId = transfer.getTargetAccountId();
        entity.amount          = transfer.getAmount();
        entity.currency        = transfer.getCurrency();
        entity.status          = transfer.getStatus();
        entity.failureReason   = transfer.getFailureReason();
        entity.idempotencyKey  = transfer.getIdempotencyKey();
        entity.createdAt       = transfer.getCreatedAt();
        entity.updatedAt       = LocalDateTime.now();
        return entity;
    }

    public Transfer toDomain() {
        return new Transfer(id, sourceAccountId, targetAccountId,
                amount, currency, status, failureReason,
                idempotencyKey, createdAt);
    }
}