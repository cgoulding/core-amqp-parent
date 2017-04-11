package com.monadiccloud.core.amqp.retrypolicy.exception;

public class ErrorResponseException extends ResponseMessageException {
    private static final long serialVersionUID = 4050522508860642811L;

    public ErrorResponseException(Throwable cause, String exchange, String routingKey, Object body) {
        super(cause, exchange, routingKey, body);
    }
}
