package com.monadiccloud.core.amqp.registration.notifier.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageBinding {
    @JsonProperty("queueName")
    private String queueName;

    @JsonProperty("routingKey")
    private String routingKey;

    public MessageBinding(String queueName, String routingKey) {
        this.queueName = queueName;
        this.routingKey = routingKey;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getRoutingKey() {
        return routingKey;
    }
}
