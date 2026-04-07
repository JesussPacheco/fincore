package com.fincore.transaction.application.service;

import com.fincore.transaction.domain.event.TransferCancelledEvent;
import com.fincore.transaction.domain.event.TransferCompletedEvent;
import com.fincore.transaction.domain.event.TransferInitiatedEvent;
import com.fincore.transaction.domain.model.Transfer;
import com.fincore.transaction.domain.model.Transfer.Currency;
import com.fincore.transaction.domain.port.AccountServiceClient;
import com.fincore.transaction.domain.port.IdempotencyPort;
import com.fincore.transaction.domain.port.TransferEventPublisher;
import com.fincore.transaction.domain.port.TransferRepository;
import com.fincore.transaction.domain.usecase.InitiateTransferUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransferApplicationService {

    private static final Logger log = LoggerFactory.getLogger(TransferApplicationService.class);

    private final InitiateTransferUseCase initiateTransferUseCase;
    private final TransferRepository      transferRepository;
    private final TransferEventPublisher  eventPublisher;
    private final AccountServiceClient    accountServiceClient;
    private final IdempotencyPort         idempotency;

    public TransferApplicationService(
            InitiateTransferUseCase initiateTransferUseCase,
            TransferRepository transferRepository,
            TransferEventPublisher eventPublisher,
            AccountServiceClient accountServiceClient,
            IdempotencyPort idempotency) {
        this.initiateTransferUseCase = initiateTransferUseCase;
        this.transferRepository      = transferRepository;
        this.eventPublisher          = eventPublisher;
        this.accountServiceClient    = accountServiceClient;
        this.idempotency             = idempotency;
    }

    @Transactional
    public Transfer initiateTransfer(UUID sourceAccountId, UUID targetAccountId,
                                     BigDecimal amount, Currency currency,
                                     String idempotencyKey) {

        Transfer transfer = initiateTransferUseCase.execute(
                sourceAccountId, targetAccountId, amount, currency, idempotencyKey
        );

        eventPublisher.publish(
                "fincore.transfer.initiated",
                new TransferInitiatedEvent(
                        transfer.getId(),
                        transfer.getSourceAccountId(),
                        transfer.getTargetAccountId(),
                        transfer.getAmount(),
                        transfer.getCurrency().name()
                )
        );

        log.info("Transfer initiated transferId={}", transfer.getId());
        return transfer;
    }

    // Called when Account Service confirms debit via Kafka
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleAccountDebited(UUID transferId) {
        transferRepository.findById(transferId)
                .ifPresent(transfer -> {
                    accountServiceClient.credit(
                            transfer.getTargetAccountId(),
                            transfer.getAmount(),
                            transfer.getCurrency().name()
                    );
                    transferRepository.updateStatus(transferId, Transfer.TransferStatus.COMPLETED);
                    idempotency.save(transfer.getIdempotencyKey(), transferId.toString(), 24);
                    eventPublisher.publish(
                            "fincore.transfer.completed",
                            new TransferCompletedEvent(
                                    transfer.getId(),
                                    transfer.getSourceAccountId(),
                                    transfer.getTargetAccountId(),
                                    transfer.getAmount()
                            )
                    );
                    log.info("Transfer completed transferId={}", transferId);
                });
    }
    // Called when Account Service reports debit failure via Kafka
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleAccountDebitFailed(UUID transferId, String reason) {
        transferRepository.findById(transferId)
                .ifPresent(transfer -> {
                    transferRepository.updateStatus(transferId, Transfer.TransferStatus.CANCELLED);
                    eventPublisher.publish(
                            "fincore.transfer.cancelled",
                            new TransferCancelledEvent(transfer.getId(), reason)
                    );
                    log.info("Transfer cancelled transferId={} reason={}", transferId, reason);
                });
    }
}