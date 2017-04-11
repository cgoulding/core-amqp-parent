package com.monadiccloud.core.amqp.consumer.handler;

import com.monadiccloud.core.amqp.consumer.LoggingUnhandledMessageHandler;
import com.monadiccloud.core.amqp.context.AmqpContext;
import com.monadiccloud.core.amqp.context.AmqpContextAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DelegatingMessageHandler extends LoggingUnhandledMessageHandler
        implements AmqpContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(DelegatingMessageHandler.class);

    private Map<Class<?>, GenericMessageHandler<?, ?>> handlers = new HashMap<>();

    public DelegatingMessageHandler() {
        this("DelegatingMessageHandler@" + UUID.randomUUID().toString());
    }

    public DelegatingMessageHandler(String consumerName) {
        super(consumerName);
    }

    public void handleMessage(final Object requestMessage) throws Throwable {
        try {
            GenericMessageHandler handler = handlers.get(requestMessage.getClass());
            if (handler == null) {
                LOGGER.error("Unable to resolve handler for message: " + requestMessage);
                return;
            }

            handler.executeOperation(requestMessage);
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void addHandler(Class requestMessage, GenericMessageHandler handler) {
        handlers.put(requestMessage, handler);
    }

    @Override
    public void setAmqpContext(AmqpContext rabbitContext) {
        handlers.values().forEach(handler -> handler.setAmqpContext(rabbitContext));
    }
}
