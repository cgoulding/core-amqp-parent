package com.monadiccloud.core.amqp.context;

public interface AmqpContextAware {
    void setAmqpContext(AmqpContext rabbitContext);
}
