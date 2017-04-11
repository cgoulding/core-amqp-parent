package com.monadiccloud.core.amqp.registration.notifier.model;

import com.fasterxml.jackson.databind.jsonschema.JsonSchema;

import java.util.List;
import java.util.UUID;

public class MessageRegistrationDto {
    private String registrationId;
    private String serviceName;
    private Class<?> messageClass;
    private String messageType;
    private String messageVersion;
    private JsonSchema messageSchema;
    private List<MessageExchangeDto> messageExchanges;
    private List<MessageQueueDto> messageQueues;

    public MessageRegistrationDto(String serviceName, Class<?> messageClass, String messageType, String messageVersion,
                                  JsonSchema messageSchema, List<MessageExchangeDto> messageExchanges, List<MessageQueueDto> messageQueues) {
        this.registrationId = UUID.randomUUID().toString();
        this.serviceName = serviceName;
        this.messageClass = messageClass;
        this.messageType = messageType;
        this.messageVersion = messageVersion;
        this.messageSchema = messageSchema;
        this.messageExchanges = messageExchanges;
        this.messageQueues = messageQueues;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Class<?> getMessageClass() {
        return messageClass;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getMessageVersion() {
        return messageVersion;
    }

    public JsonSchema getMessageSchema() {
        return messageSchema;
    }

    public List<MessageExchangeDto> getMessageExchanges() {
        return messageExchanges;
    }

    public List<MessageQueueDto> getMessageQueues() {
        return messageQueues;
    }
}
