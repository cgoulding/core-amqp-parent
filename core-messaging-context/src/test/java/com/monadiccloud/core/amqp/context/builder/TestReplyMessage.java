package com.monadiccloud.core.amqp.context.builder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.monadiccloud.core.amqp.message.annotation.Message;
import com.monadiccloud.core.amqp.message.annotation.stereotypes.MessageReply;

@Message(value = "test.message.reply", version = "1.0")
@MessageReply
public class TestReplyMessage {
    @JsonProperty("correlationId")
    private String correlationId;

    @JsonProperty("reply-to")
    private String replyTo;

    @JsonProperty("asdf")
    private String asdf;

    public TestReplyMessage(String correlationId, String replyTo, String asdf) {
        this.correlationId = correlationId;
        this.replyTo = replyTo;
        this.asdf = asdf;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public String getAsdf() {
        return asdf;
    }
}
