package com.monadiccloud.core.amqp.context.builder;

import java.util.List;

/**
 * @author Connor Goulding
 */
public class MessagingData {
    private List<MessageMetaData> messages;

    public List<MessageMetaData> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageMetaData> messages) {
        this.messages = messages;
    }
}
