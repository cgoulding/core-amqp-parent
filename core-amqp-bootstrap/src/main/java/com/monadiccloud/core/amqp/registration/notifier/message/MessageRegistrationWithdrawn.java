package com.monadiccloud.core.amqp.registration.notifier.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.monadiccloud.core.amqp.message.annotation.Message;

import java.util.Date;

@Message(value = "common.message.registration.withdrawn", version = "1.0")
public class MessageRegistrationWithdrawn {
    @JsonProperty("correlationId")
    private String correlationId;

    @JsonProperty("timestamp")
    private Date timestamp;

    @JsonProperty("registrationId")
    private String registrationId;

    public MessageRegistrationWithdrawn(String correlationId, Date timestamp, String registrationId) {
        this.correlationId = correlationId;
        this.timestamp = timestamp;
        this.registrationId = registrationId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getRegistrationId() {
        return registrationId;
    }
}
