package com.monadiccloud.core.amqp.message;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import java.text.SimpleDateFormat;

public class MessageConverterFactory {
    private static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

    public static MessageConverter createDefaultConverter(final ClassMapper classMapper) {
        final Jackson2JsonMessageConverter messageConverter =
                new Jackson2JsonMessageConverter();
        messageConverter.setClassMapper(classMapper);
        messageConverter.setCreateMessageIds(true);

        final ObjectMapper objectMapper = new ObjectMapper();

        // use ISO8601 format for dates
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new SimpleDateFormat(ISO8601_DATE_FORMAT));

        // ignore properties we don't need or aren't expecting
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        messageConverter.setJsonObjectMapper(objectMapper);

        return messageConverter;
    }
}
