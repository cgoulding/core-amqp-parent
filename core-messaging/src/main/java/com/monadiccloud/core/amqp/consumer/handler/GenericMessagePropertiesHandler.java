package com.monadiccloud.core.amqp.consumer.handler;

import com.monadiccloud.core.amqp.consumer.LoggingUnhandledMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageProperties;

import java.util.UUID;

public abstract class GenericMessagePropertiesHandler<R extends Object, E extends Throwable> extends LoggingUnhandledMessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericMessagePropertiesHandler.class);

    public GenericMessagePropertiesHandler() {
        this("GenericMessageHandler@" + UUID.randomUUID().toString());
    }

    public GenericMessagePropertiesHandler(String consumerName) {
        super(consumerName);
    }

    protected abstract void executeOperation(final R requestMessage,
                                             final MessageProperties messageProperties) throws Throwable;


    public void handleMessage(final R requestMessage,
                              final MessageProperties messageProperties) throws E {
        try {
            executeOperation(requestMessage, messageProperties);
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            cleanup(requestMessage);
        }
    }


    protected void cleanup(final R requestMessage) {
        // do nothing by default
    }


    protected abstract E convertException(Throwable t);
}
