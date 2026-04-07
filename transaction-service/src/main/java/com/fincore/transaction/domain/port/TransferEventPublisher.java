package com.fincore.transaction.domain.port;

public interface TransferEventPublisher {

    void publish(String topic, Object event);
}