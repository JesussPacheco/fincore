package com.fincore.account.domain.port;

public interface AccountEventPublisher {

    void publish(String topic, Object event);
}