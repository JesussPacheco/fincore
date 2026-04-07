package com.fincore.transaction.infrastructure.web;

import com.fincore.transaction.domain.model.Transfer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferResponse(
        UUID transferId,
        UUID sourceAccountId,
        UUID targetAccountId,
        BigDecimal amount,
        String currency,
        String status,
        LocalDateTime createdAt
) {
    public static TransferResponse fromDomain(Transfer transfer) {
        return new TransferResponse(
                transfer.getId(),
                transfer.getSourceAccountId(),
                transfer.getTargetAccountId(),
                transfer.getAmount(),
                transfer.getCurrency().name(),
                transfer.getStatus().name(),
                transfer.getCreatedAt()
        );
    }
}