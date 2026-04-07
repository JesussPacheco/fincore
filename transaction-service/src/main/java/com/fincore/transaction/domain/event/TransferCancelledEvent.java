package com.fincore.transaction.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class TransferCancelledEvent {

    private final UUID eventId;
    private final UUID transferId;
    private final String reason;
    private final LocalDateTime occurredAt;

    public TransferCancelledEvent(UUID transferId, String reason) {
        this.eventId    = UUID.randomUUID();
        this.transferId = transferId;
        this.reason     = reason;
        this.occurredAt = LocalDateTime.now();
    }

    public UUID getEventId()             { return eventId; }
    public UUID getTransferId()          { return transferId; }
    public String getReason()            { return reason; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
}