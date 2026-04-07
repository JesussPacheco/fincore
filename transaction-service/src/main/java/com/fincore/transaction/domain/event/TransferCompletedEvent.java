package com.fincore.transaction.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransferCompletedEvent {

    private final UUID eventId;
    private final UUID transferId;
    private final UUID sourceAccountId;
    private final UUID targetAccountId;
    private final BigDecimal amount;
    private final LocalDateTime occurredAt;

    public TransferCompletedEvent(UUID transferId, UUID sourceAccountId,
                                  UUID targetAccountId, BigDecimal amount) {
        this.eventId         = UUID.randomUUID();
        this.transferId      = transferId;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount          = amount;
        this.occurredAt      = LocalDateTime.now();
    }

    public UUID getEventId()            { return eventId; }
    public UUID getTransferId()         { return transferId; }
    public UUID getSourceAccountId()    { return sourceAccountId; }
    public UUID getTargetAccountId()    { return targetAccountId; }
    public BigDecimal getAmount()       { return amount; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
}