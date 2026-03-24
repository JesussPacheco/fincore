package com.fincore.auth.domain.port;

public interface EventPublisher {

    void publish(String topic, Object event);
}