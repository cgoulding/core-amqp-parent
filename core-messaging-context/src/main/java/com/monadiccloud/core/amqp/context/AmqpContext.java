package com.monadiccloud.core.amqp.context;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.support.converter.AbstractJsonMessageConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AmqpContext {
    private final String contextUuid;
    private final RabbitAdmin admin;
    private final RabbitTemplate rabbitTemplate;
    private final AbstractJsonMessageConverter messageConverter;

    private final Collection<Exchange> exchanges = new ArrayList<>();
    private final Collection<Queue> queues = new ArrayList<>();
    private final Collection<Binding> bindings = new ArrayList<>();
    private final Collection<MessageListenerContainer> containers = new ArrayList<>();
    private final Map<Class, MessageDescription> descriptionLookup = new HashMap<>();
    private final Map<RequestReplyKey, String> replyToLookup = new HashMap<>();

    public AmqpContext(String contextUuid, RabbitAdmin admin, RabbitTemplate rabbitTemplate, AbstractJsonMessageConverter messageConverter,
                       Collection<Exchange> exchanges, Collection<Queue> queues, Collection<Binding> bindings,
                       Collection<MessageDescription> descriptions, Collection<MessageListenerContainer> containers,
                       Map<RequestReplyKey, String> replyToLookup) {
        this.contextUuid = contextUuid;
        this.admin = admin;
        this.rabbitTemplate = rabbitTemplate;
        this.messageConverter = messageConverter;

        addAll(this.exchanges, exchanges);
        addAll(this.queues, queues);
        addAll(this.bindings, bindings);
        addAll(this.containers, containers);
        putAll(this.replyToLookup, replyToLookup);

        descriptions.forEach(d -> descriptionLookup.put(d.getMessageClass(), d));
    }

    private void addAll(Collection source, Collection additions) {
        if (additions != null) {
            source.addAll(additions);
        }
    }

    private void putAll(Map source, Map additions) {
        if (additions != null) {
            source.putAll(additions);
        }
    }

    public void declare() {
        exchanges.forEach(exchange -> admin.declareExchange(exchange));
        queues.forEach(queue -> admin.declareQueue(queue));
        bindings.forEach(binding -> admin.declareBinding(binding));
    }

    public void start() {
        containers.forEach(container -> container.start());
    }

    public String getContextUuid() {
        return contextUuid;
    }

    public RabbitAdmin getAdmin() {
        return admin;
    }

    public RabbitTemplate getRabbitTemplate() {
        return rabbitTemplate;
    }

    public AbstractJsonMessageConverter getMessageConverter() {
        return messageConverter;
    }

    public Collection<Exchange> getExchanges() {
        return exchanges;
    }

    public Collection<Queue> getQueues() {
        return queues;
    }

    public Collection<Binding> getBindings() {
        return bindings;
    }

    public MessageDescription getDescription(Class messageClass) {
        return descriptionLookup.get(messageClass);
    }

    public Collection<MessageListenerContainer> getContainers() {
        return containers;
    }

    public String getReplyTo(Class request, Class reply) {
        return replyToLookup.get(new RequestReplyKey(request, reply));
    }
}
