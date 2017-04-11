package com.monadiccloud.core.amqp.consumer.error;

public class ErrorContext<M> {
    protected M requestMessage;
    protected String errorRoutingKeyPrefix;

    public ErrorContext(M requestMessage, String errorRoutingKeyPrefix) {
        this.requestMessage = requestMessage;
        this.errorRoutingKeyPrefix = errorRoutingKeyPrefix;
    }

    public M getRequestMessage() {
        return requestMessage;
    }

    public String getErrorRoutingKeyPrefix() {
        return errorRoutingKeyPrefix;
    }
}
