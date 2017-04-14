package com.monadiccloud.core.amqp.consumer.handler;

import com.monadiccloud.core.amqp.context.AmqpContext;
import com.monadiccloud.core.amqp.context.AmqpContextAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericMessageHandler<R extends Object, E extends Throwable> implements AmqpContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericMessageHandler.class);

    protected AmqpContext rabbitContext;

    protected abstract void executeOperation(final R requestMessage) throws Throwable;

    public void handleMessage(final R requestMessage) throws E {
        try {
            executeOperation(requestMessage);
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
            throw convertException(e);
        }
    }

    protected abstract E convertException(Throwable t);

    @Override
    public void setAmqpContext(AmqpContext rabbitContext) {
        this.rabbitContext = rabbitContext;
    }
}
