package com.monadiccloud.core.amqp.consumer.handler;

import com.monadiccloud.core.amqp.context.AmqpContext;
import com.monadiccloud.core.amqp.context.AmqpContextAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DelegatingMessageHandler implements AmqpContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(DelegatingMessageHandler.class);

    private Map<Class<?>, AmqpContextAwareMessageHandler> handlers = new HashMap<>();

    public void handleMessage(final Object requestMessage) throws Throwable {
        try {
            AmqpContextAwareMessageHandler handler = handlers.get(requestMessage.getClass());
            if (handler == null) {
                LOGGER.error("Unable to resolve handler for message: " + requestMessage);
                return;
            }

            handler.handleMessage(requestMessage);
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void addHandler(Class requestMessage, AmqpContextAwareMessageHandler handler) {
        handlers.put(requestMessage, handler);
    }

    @Override
    public void setAmqpContext(AmqpContext rabbitContext) {
        handlers.values().forEach(handler -> handler.setAmqpContext(rabbitContext));
    }
}
