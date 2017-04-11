package com.monadiccloud.core.amqp.exceptions;

/**
 * @author Connor Goulding
 */
public class RabbitMQException extends Exception {
    public RabbitMQException(String message) {
        super(message);
    }

    public RabbitMQException(String message, Throwable cause) {
        super(message, cause);
    }
}
