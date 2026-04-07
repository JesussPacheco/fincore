package com.fincore.transaction.infrastructure.web;

import com.fincore.transaction.application.service.TransferApplicationService;
import com.fincore.transaction.domain.model.Transfer;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private static final Logger log = LoggerFactory.getLogger(TransferController.class);

    private final TransferApplicationService transferApplicationService;

    public TransferController(TransferApplicationService transferApplicationService) {
        this.transferApplicationService = transferApplicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TransferResponse initiateTransfer(
            @Valid @RequestBody TransferRequest request,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @RequestHeader("X-User-Id") String userId) {

        log.info("Transfer request userId={} sourceAccountId={} targetAccountId={} amount={}",
                userId,
                request.sourceAccountId(),
                request.targetAccountId(),
                request.amount());

        Transfer transfer = transferApplicationService.initiateTransfer(
                request.sourceAccountId(),
                request.targetAccountId(),
                request.amount(),
                request.currency(),
                idempotencyKey
        );

        return TransferResponse.fromDomain(transfer);
    }
}