package com.monadiccloud.core.amqp.registration.notifier.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.monadiccloud.core.amqp.MessageAnnotationProcessor;
import com.monadiccloud.core.amqp.MessageAnnotationProcessorCallback;
import com.monadiccloud.core.amqp.registration.notifier.message.MessageRegistrationNotified;
import com.monadiccloud.core.amqp.registration.notifier.message.MessageRegistrationWithdrawn;
import com.monadiccloud.core.amqp.registration.notifier.service.AmqpRegistrationNotifierService;
import com.monadiccloud.core.amqp.registration.notifier.service.RegistrationNotifierService;
import com.monadiccloud.core.amqp.registration.notifier.spring.ApplicationContextRegistrationListener;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@Configuration
public class RegistrationConfig {
    public static final String EXCHANGE_COMMON_REGISTRATION_EVENT = "exchange.monadiccloud.core.registration.event";

    private static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";

    private static final int MAX_ATTEMPTS = 10;
    private static final int INITIAL_INTERVAL = 100;
    private static final double MULTIPLIER = 2.0;
    private static final int MAX_INTERVAL = 50000;

    @Autowired
    private ConnectionFactory rabbitConnectionFactory;

    @Bean
    ApplicationContextRegistrationListener registrationListener() {
        return new ApplicationContextRegistrationListener();
    }

    @Bean
    RegistrationNotifierService registrationNotifierService() {
        return new AmqpRegistrationNotifierService(registrationObjectMapper(), registrationRabbitTemplate(),
                EXCHANGE_COMMON_REGISTRATION_EVENT, null);
    }

    @Bean
    TopicExchange registrationEventExchange() {
        return new TopicExchange(EXCHANGE_COMMON_REGISTRATION_EVENT);
    }

    private RabbitTemplate registrationRabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(rabbitConnectionFactory);
        template.setMessageConverter(registrationMessageConverter());
        template.setRetryTemplate(registrationRetryTemplate());
        return template;
    }

    public MessageConverter registrationMessageConverter() {
        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        messageConverter.setClassMapper(registrationClassMapper());
        messageConverter.setCreateMessageIds(true);
        messageConverter.setJsonObjectMapper(registrationObjectMapper());

        return messageConverter;
    }

    private RetryTemplate registrationRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(INITIAL_INTERVAL);
        backOffPolicy.setMultiplier(MULTIPLIER);
        backOffPolicy.setMaxInterval(MAX_INTERVAL);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(MAX_ATTEMPTS);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    private ClassMapper registrationClassMapper() {
        //stub
        DefaultClassMapper classMapper = new DefaultClassMapper();
        final Map<String, Class<?>> classMappings = new HashMap<>();

        List<Class<?>> messageClasses = asList(MessageRegistrationNotified.class, MessageRegistrationWithdrawn.class);

        MessageAnnotationProcessor messageAnnotationProcessor = new MessageAnnotationProcessor();

        messageAnnotationProcessor.process(new MessageAnnotationProcessorCallback() {
            @Override
            public void found(String messageType, Class messageClass) {
                classMappings.put(messageType, messageClass);
            }

        }, messageClasses);

        classMapper.setIdClassMapping(classMappings);

        return classMapper;
    }

    ObjectMapper registrationObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();

        // use ISO8601 format for dates
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        objectMapper.setDateFormat(new SimpleDateFormat(ISO8601_DATE_FORMAT));

        // ignore properties we don't need or aren't expecting
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
