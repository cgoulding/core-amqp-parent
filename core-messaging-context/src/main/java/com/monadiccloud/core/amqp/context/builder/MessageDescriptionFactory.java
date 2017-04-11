package com.monadiccloud.core.amqp.context.builder;

import com.monadiccloud.core.amqp.context.ApplicationConfiguration;
import com.monadiccloud.core.amqp.context.MessageDescription;
import com.monadiccloud.core.amqp.message.annotation.Message;
import com.monadiccloud.core.amqp.message.annotation.MessageContentType;
import com.monadiccloud.core.amqp.message.annotation.stereotypes.MessageEvent;
import com.monadiccloud.core.amqp.message.annotation.stereotypes.MessageReply;
import com.monadiccloud.core.amqp.message.annotation.stereotypes.MessageRequest;
import com.monadiccloud.core.amqp.message.annotation.stereotypes.MessageStereotype;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MessageDescriptionFactory {
    public static final String PROVIDER_ID_PLACEHOLDER = "{providerId}";

    private final ApplicationConfiguration applicationConfiguration;
    private final Map<String, MessageMetaData> metaDatas = new HashMap<>();

    public MessageDescriptionFactory(ApplicationConfiguration applicationConfiguration, Collection<MessageMetaData> metaDatas) {
        this.applicationConfiguration = applicationConfiguration;
        if (metaDatas != null) {
            metaDatas.forEach(metaData -> this.metaDatas.put(metaData.getMessage(), metaData));
        }
    }

    public <M> MessageDescription<M> createDescription(Class<M> messageClass) {
        String type = null;
        String version = null;
        MessageStereotype stereotype = null;
        MessageContentType contentType = null;

        String exchange = null;
        MessageExchangeType exchangeType = null;
        String routingKey = null;

        // Basic Message Information
        Message messageAnnotation = messageClass.getAnnotation(Message.class);
        type = messageAnnotation.value();
        version = messageAnnotation.version();
        contentType = messageAnnotation.content();

        // Opinion
        MessageMetaData metaData = metaDatas.get(type);
        if (metaData != null) {
            exchange = applicationFlavouredExchange(metaData.getExchange());
            if (!StringUtils.isBlank(metaData.getExchangeType())) {
                exchangeType = MessageExchangeType.valueOf(metaData.getExchangeType());
            } else {
                exchangeType = MessageExchangeType.TOPIC;
            }

            if (!StringUtils.isBlank(metaData.getRoutingKey())) {
                routingKey = metaData.getRoutingKey();
            }
        }

        // Stereotype
        MessageRequest requestAnnotation = messageClass.getAnnotation(MessageRequest.class);
        if (requestAnnotation != null) {
            stereotype = requestAnnotation.stereotype();
        }

        // Stereotype
        MessageReply replyAnnotation = messageClass.getAnnotation(MessageReply.class);
        if (replyAnnotation != null) {
            stereotype = replyAnnotation.stereotype();
        }

        // Stereotype
        MessageEvent eventAnnotation = messageClass.getAnnotation(MessageEvent.class);
        if (eventAnnotation != null) {
            stereotype = eventAnnotation.stereotype();
        }

        MessageDescription<M> description = new MessageDescription<M>();
        description.setExchange(exchange);
        description.setExchangeType(exchangeType);
        description.setMessageClass(messageClass);
        description.setRoutingKey(routingKey);
        description.setStereotype(stereotype);
        description.setType(type);
        description.setVersion(version);
        description.setContentType(contentType);
        return description;
    }

    private String applicationFlavouredExchange(String exchange) {
        if (exchange != null) {
            if (exchange.contains(PROVIDER_ID_PLACEHOLDER)) {
                // This can be tuned later if necessary
                return exchange.replace("{providerId}", applicationConfiguration.getApplicationName());
            }
        }
        return exchange;
    }
}
