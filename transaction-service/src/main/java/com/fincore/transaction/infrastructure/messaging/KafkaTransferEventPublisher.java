package com.fincore.transaction.infrastructure.messaging;

import com.fincore.transaction.domain.port.TransferEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class KafkaTransferEventPublisher implements TransferEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaTransferEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaTransferEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(String topic, Object event) {
        kafkaTemplate.send(topic, event)
                .whenComplete((result, ex) ->
                        Optional.ofNullable(ex)
                                .ifPresentOrElse(
                                        e -> log.error("Failed to publish event topic={} error={}",
                                                topic, e.getMessage()),
                                        () -> log.info("Event published topic={} offset={}",
                                                topic, result.getRecordMetadata().offset())
                                )
                );
    }
}