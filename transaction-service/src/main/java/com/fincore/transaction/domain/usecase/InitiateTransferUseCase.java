package com.fincore.transaction.domain.usecase;

import com.fincore.transaction.domain.exception.DuplicateTransferException;
import com.fincore.transaction.domain.exception.TransferLockException;
import com.fincore.transaction.domain.model.Transfer;
import com.fincore.transaction.domain.model.Transfer.Currency;
import com.fincore.transaction.domain.port.AccountServiceClient;
import com.fincore.transaction.domain.port.DistributedLockPort;
import com.fincore.transaction.domain.port.IdempotencyPort;
import com.fincore.transaction.domain.port.TransferRepository;

import java.math.BigDecimal;
import java.util.UUID;

public class InitiateTransferUseCase {

    private final TransferRepository    transferRepository;
    private final AccountServiceClient  accountServiceClient;
    private final DistributedLockPort   distributedLock;
    private final IdempotencyPort       idempotency;

    public InitiateTransferUseCase(TransferRepository transferRepository,
                                   AccountServiceClient accountServiceClient,
                                   DistributedLockPort distributedLock,
                                   IdempotencyPort idempotency) {
        this.transferRepository  = transferRepository;
        this.accountServiceClient = accountServiceClient;
        this.distributedLock     = distributedLock;
        this.idempotency         = idempotency;
    }

    public Transfer execute(UUID sourceAccountId, UUID targetAccountId,
                            BigDecimal amount, Currency currency,
                            String idempotencyKey) {

        // Check idempotency — return cached result if already processed
        idempotency.get(idempotencyKey)
                .ifPresent(cached -> {
                    throw new DuplicateTransferException(idempotencyKey);
                });

        // Acquire distributed lock on source account
        String lockKey = "lock:account:" + sourceAccountId;
        boolean locked = distributedLock.acquireLock(lockKey, 5, 10);

        if (!locked) {
            throw new TransferLockException(sourceAccountId.toString());
        }

        try {
            // Validate balance via HTTP sync call to Account Service
           accountServiceClient.validateBalance(sourceAccountId, amount, currency.name());

            // Create and persist transfer
            Transfer transfer = new Transfer(
                    sourceAccountId, targetAccountId,
                    amount, currency, idempotencyKey
            );

            return transferRepository.save(transfer);

        } finally {
            distributedLock.releaseLock(lockKey);
        }
    }
}