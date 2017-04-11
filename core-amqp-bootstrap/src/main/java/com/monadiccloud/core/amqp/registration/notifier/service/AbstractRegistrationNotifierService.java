package com.monadiccloud.core.amqp.registration.notifier.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monadiccloud.core.amqp.registration.notifier.message.MessageBinding;
import com.monadiccloud.core.amqp.registration.notifier.message.MessageExchange;
import com.monadiccloud.core.amqp.registration.notifier.message.MessageRegistrationNotified;
import com.monadiccloud.core.amqp.registration.notifier.message.MessageRegistrationWithdrawn;
import com.monadiccloud.core.amqp.registration.notifier.model.BindingDataDto;
import com.monadiccloud.core.amqp.registration.notifier.model.MessageExchangeDto;
import com.monadiccloud.core.amqp.registration.notifier.model.MessageRegistrationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.UUID;
import java.util.stream.Collectors;

public class AbstractRegistrationNotifierService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRegistrationNotifierService.class);

    protected MessageRegistrationNotified transformNotified(MessageRegistrationDto entry) {
        return new MessageRegistrationNotified(UUID.randomUUID().toString(), Calendar.getInstance().getTime(), entry.getRegistrationId(),
                entry.getServiceName(), entry.getMessageType(), entry.getMessageVersion(), entry.getMessageSchema(),
                entry.getMessageExchanges() == null ? null : entry.getMessageExchanges().stream().map(this::transformMessageExchange).collect(Collectors.toList()));
    }

    protected MessageRegistrationWithdrawn transformWithdrawn(MessageRegistrationDto entry) {
        return new MessageRegistrationWithdrawn(UUID.randomUUID().toString(), Calendar.getInstance().getTime(), entry.getRegistrationId());
    }

    protected MessageExchange transformMessageExchange(MessageExchangeDto exchange) {
        return new MessageExchange(exchange.getName(), String.valueOf(exchange.getDirection()),
                exchange.getBindings() == null ? null : exchange.getBindings().stream().map(this::transformBindingData).collect(Collectors.toList()));
    }

    protected MessageBinding transformBindingData(BindingDataDto binding) {
        return new MessageBinding(binding.getQueueName(), binding.getRoutingKey());
    }

    protected void log(ObjectMapper objectMapper, Object object) {
        try {
            LOGGER.info(objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to log message", e);
        }
    }
}
