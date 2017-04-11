package com.monadiccloud.core.amqp.template;

import com.monadiccloud.core.amqp.context.AmqpContext;
import com.monadiccloud.core.amqp.context.AmqpContextAware;
import com.monadiccloud.core.amqp.context.MessageDescription;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class OpinionatedRabbitTemplate implements AmqpContextAware {
    private AmqpContext rabbitContext;
    private RabbitTemplate rabbitTemplate;

    public void send(Object message) {
        MessageDescription description = rabbitContext.getDescription(message.getClass());
        rabbitTemplate.convertAndSend(description.getExchange(), description.getRoutingKey(), message);
    }

    public void send(Object message, String routingKey) {
        MessageDescription description = rabbitContext.getDescription(message.getClass());
        rabbitTemplate.convertAndSend(description.getExchange(), routingKey, message);
    }

    @Override
    public void setAmqpContext(AmqpContext rabbitContext) {
        this.rabbitContext = rabbitContext;
        this.rabbitTemplate = rabbitContext.getRabbitTemplate();
    }
}
