package com.fincore.account.infrastructure.messaging;

import com.fincore.account.application.service.AccountApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class TransferEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransferEventConsumer.class);

    private final AccountApplicationService accountApplicationService;

    public TransferEventConsumer(AccountApplicationService accountApplicationService) {
        this.accountApplicationService = accountApplicationService;
    }

    @KafkaListener(topics = "fincore.transfer.initiated",
            groupId = "account-service-group")
    public void handleTransferInitiated(Map<String, Object> event) {
        UUID transferId      = UUID.fromString(event.get("transferId").toString());
        UUID sourceAccountId = UUID.fromString(event.get("sourceAccountId").toString());
        BigDecimal amount    = new BigDecimal(event.get("amount").toString());
        String currency      = event.get("currency").toString();

        log.info("Transfer initiated event received transferId={} sourceAccountId={}",
                transferId, sourceAccountId);

        try {
            accountApplicationService.debitForTransfer(
                    sourceAccountId, transferId, amount, currency);
        } catch (Exception e) {
            log.error("Failed to process transfer transferId={} error={}",
                    transferId, e.getMessage());
        }
    }
}