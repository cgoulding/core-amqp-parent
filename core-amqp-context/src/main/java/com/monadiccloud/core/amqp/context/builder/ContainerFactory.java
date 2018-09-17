package com.monadiccloud.core.amqp.context.builder;

import com.monadiccloud.core.amqp.retrypolicy.RetryPolicyFactory;
import org.aopalliance.aop.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.util.ErrorHandler;

public class ContainerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerFactory.class);

    public SimpleMessageListenerContainer createDefaultContainer(
            String containerName,
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter,
            MessagePropertiesConverter messagePropertiesConverter,
            Object messageHandler) {
        final SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setAdviceChain(new Advice[]{RetryPolicyFactory.createListenerRetryPolicy()});
        container.setErrorHandler(errorHandler(containerName));
        container.setMessagePropertiesConverter(messagePropertiesConverter);
        container.setMessageConverter(messageConverter);
        container.setMessageListener(new MessageListenerAdapter(messageHandler, messageConverter));
        return container;
    }

    private ErrorHandler errorHandler(final String listenerName) {
        return new DefaultContainerErrorHandler(listenerName);
    }
}
