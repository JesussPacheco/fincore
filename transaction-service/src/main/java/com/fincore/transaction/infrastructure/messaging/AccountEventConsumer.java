package com.fincore.transaction.infrastructure.messaging;

import com.fincore.transaction.application.service.TransferApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class AccountEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(AccountEventConsumer.class);

    private final TransferApplicationService transferApplicationService;

    public AccountEventConsumer(TransferApplicationService transferApplicationService) {
        this.transferApplicationService = transferApplicationService;
    }

    @KafkaListener(topics = "fincore.account.debited",
            groupId = "transaction-service-group")
    public void handleAccountDebited(Map<String, Object> event) {
        UUID transferId = UUID.fromString(event.get("transferId").toString());
        log.info("Account debited event received transferId={}", transferId);
        try {
            transferApplicationService.handleAccountDebited(transferId);
        } catch (Exception e) {
            log.error("Failed to handle account debited transferId={} error={}",
                    transferId, e.getMessage());
        }
    }

    @KafkaListener(topics = "fincore.account.debit.failed",
            groupId = "transaction-service-group")
    public void handleAccountDebitFailed(Map<String, Object> event) {
        UUID transferId = UUID.fromString(event.get("transferId").toString());
        String reason   = event.get("reason").toString();
        log.info("Account debit failed event received transferId={}", transferId);
        try {
            transferApplicationService.handleAccountDebitFailed(transferId, reason);
        } catch (Exception e) {
            log.error("Failed to handle account debit failed transferId={} error={}",
                    transferId, e.getMessage());
        }
    }
}