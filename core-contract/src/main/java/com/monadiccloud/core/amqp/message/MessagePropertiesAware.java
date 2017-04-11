package com.monadiccloud.core.amqp.message;

public interface MessagePropertiesAware<T extends MessagePropertiesContainer> {
    T getMessageProperties();

    void setMessageProperties(T messageProperties);
}
