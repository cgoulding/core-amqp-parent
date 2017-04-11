package com.monadiccloud.core.amqp.registration.notifier.model;

public class MessageQueueDto {
    private String name;

    public MessageQueueDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
