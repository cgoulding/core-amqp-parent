package com.monadiccloud.core.amqp.consumer.handler;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;

public class DefaultMessageListenerAdapter extends MessageListenerAdapter {
    public DefaultMessageListenerAdapter(Object delegate, MessageConverter messageConverter) {
        super(delegate, messageConverter);
    }

    @Override
    protected Object invokeListenerMethod(String methodName, Object[] arguments, Message originalMessage)
            throws Exception {
        final MessageProperties originalMessageProperties = originalMessage.getMessageProperties();

        int size = arguments.length;

        Object[] enrichedArguments = new Object[size + 1];

        System.arraycopy(arguments, 0, enrichedArguments, 0, size);

        enrichedArguments[size] = originalMessageProperties;

        return super.invokeListenerMethod(methodName, enrichedArguments, originalMessage);
    }
}
