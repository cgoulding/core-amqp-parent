package com.monadiccloud.core.amqp.consumer.handler;

import com.monadiccloud.core.amqp.context.AmqpContextAware;

/**
 * @author Connor Goulding
 */
public interface AmqpContextAwareMessageHandler extends AmqpContextAware {
    void handleMessage(final Object message) throws Exception;
}
