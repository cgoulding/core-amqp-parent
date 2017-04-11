package com.monadiccloud.core.amqp.consumer;

public abstract class UnhandledMessageConsumer {
    private UnhandledMessageHandler unhandledMessageHandler = null;

    public UnhandledMessageConsumer() {
        super();

        this.unhandledMessageHandler =
                new LoggingUnhandledMessageHandler(this.getClass().getName());
    }

    public void setUnhandledMessageHandler(UnhandledMessageHandler unhandledMessageHandler) {
        this.unhandledMessageHandler = unhandledMessageHandler;
    }

    public void handleMessage(byte[] messagePayload) {
        if (unhandledMessageHandler != null) {
            unhandledMessageHandler.unhandledMessage(messagePayload);
        }
    }
}
