package com.monadiccloud.core.amqp.consumer.error;

public interface ErrorTransformer<M> {
    Exception transform(Exception e, ErrorContext<M> context);
}
