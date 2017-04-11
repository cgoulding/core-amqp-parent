package com.monadiccloud.core.amqp.context.builder;

import com.monadiccloud.core.amqp.exceptions.ExceptionLogTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.ImmediateAcknowledgeAmqpException;
import org.springframework.util.ErrorHandler;

public class DefaultContainerErrorHandler implements ErrorHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultContainerErrorHandler.class);

    protected ExceptionLogTransformer exceptionTransformer = new ExceptionLogTransformer();

    private String listenerName;

    public DefaultContainerErrorHandler(String listenerName) {
        this.listenerName = listenerName;
    }

    @Override
    public void handleError(Throwable cause) {
        cause = exceptionTransformer.transform(cause);

        if (cause instanceof ImmediateAcknowledgeAmqpException) {
            LOGGER.info("Handling error", cause);
            return;
        }

        LOGGER.error("Handling error", cause);
    }
};
