package com.monadiccloud.core.amqp.message;

public interface CorrelationIdAware {
    String getCorrelationId();

    void setCorrelationId(String correlationId);
}
