package com.monadiccloud.core.amqp.retrypolicy;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.retry.MessageKeyGenerator;

public class DefaultMessageKeyGenerator implements MessageKeyGenerator {
    private static final String UNDEFINED_MESSAGE_KEY = "UNDEFINED";

    @Override
    public Object getKey(Message message) {
        if (message.getMessageProperties().getMessageId() != null) {
            return message.getMessageProperties().getMessageId();
        } else {
            return UNDEFINED_MESSAGE_KEY;
        }
    }
}
