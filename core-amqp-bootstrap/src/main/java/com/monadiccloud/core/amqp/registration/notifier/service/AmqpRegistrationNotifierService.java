package com.monadiccloud.core.amqp.registration.notifier.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monadiccloud.core.amqp.registration.notifier.message.MessageRegistrationNotified;
import com.monadiccloud.core.amqp.registration.notifier.message.MessageRegistrationWithdrawn;
import com.monadiccloud.core.amqp.registration.notifier.model.MessageRegistrationDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class AmqpRegistrationNotifierService extends AbstractRegistrationNotifierService implements RegistrationNotifierService {
    private RabbitTemplate template;
    private String notificationExchange;
    private String routingKey;
    private ObjectMapper objectMapper;

    public AmqpRegistrationNotifierService(ObjectMapper objectMapper, RabbitTemplate template, String notificationExchange, String routingKey) {
        this.objectMapper = objectMapper;
        this.template = template;
        this.notificationExchange = notificationExchange;
        this.routingKey = routingKey;
    }

    @Override
    public void notify(MessageRegistrationDto entry) {
        MessageRegistrationNotified notified = transformNotified(entry);
        template.convertAndSend(notificationExchange, routingKey, notified);
        log(objectMapper, notified);
    }

    @Override
    public void withdraw(MessageRegistrationDto entry) {
        MessageRegistrationWithdrawn withdrawn = transformWithdrawn(entry);
        template.convertAndSend(notificationExchange, routingKey, withdrawn);
        log(objectMapper, withdrawn);
    }

}