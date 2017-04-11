package com.monadiccloud.core.amqp.consumer.handler;

import org.springframework.amqp.core.Message;

public interface MessageHandler<T> {
    boolean canHandle(Message message, Object body);

    void handleMessage(T message) throws Exception;
}
