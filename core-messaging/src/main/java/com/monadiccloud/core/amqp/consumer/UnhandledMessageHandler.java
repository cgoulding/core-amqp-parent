package com.monadiccloud.core.amqp.consumer;

public interface UnhandledMessageHandler {
    void unhandledMessage(byte[] messagePayload);
}
