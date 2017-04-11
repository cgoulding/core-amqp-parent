package com.monadiccloud.core.amqp;

import com.monadiccloud.core.amqp.message.MessagePropertiesContainer;

import java.util.Date;

public class TestMessageProperties implements MessagePropertiesContainer {
    private Date timestamp;
    private String correlationId;
    private String replyTo;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }
}
