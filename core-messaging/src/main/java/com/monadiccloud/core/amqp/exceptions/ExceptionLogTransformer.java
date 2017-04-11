package com.monadiccloud.core.amqp.exceptions;

import com.monadiccloud.core.amqp.retrypolicy.exception.ErrorResponseException;
import org.springframework.amqp.rabbit.listener.exception.ListenerExecutionFailedException;

public class ExceptionLogTransformer {
    public Throwable transform(Throwable cause) {
        if (cause instanceof ListenerExecutionFailedException && cause.getCause() != null) {
            cause = cause.getCause();
        }
        if (cause instanceof ErrorResponseException && cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }
}
