package com.monadiccloud.core.amqp;

public interface MessageAnnotationProcessorCallback {
    void found(String messageType, Class messageClass);
}
