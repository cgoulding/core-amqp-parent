package com.monadiccloud.core.amqp.registration;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.monadiccloud.core.amqp.MessageAnnotationProcessor;
import com.monadiccloud.core.amqp.registration.notifier.model.MessageDirectionType;
import com.monadiccloud.core.amqp.registration.notifier.model.MessageExchangeDto;
import com.monadiccloud.core.amqp.registration.notifier.model.MessageQueueDto;
import com.monadiccloud.core.amqp.registration.notifier.model.MessageRegistrationDto;
import org.springframework.amqp.support.converter.DefaultClassMapper;

import java.io.IOException;
import java.util.*;

public class RegistrationAwareClassMapper extends DefaultClassMapper implements MessageRegistrationAware {
    private String serviceName;
    private boolean autoRegister;

    private Map<String, MessageRegistrationDto> classRegistrations = new HashMap<>();

    public RegistrationAwareClassMapper(String serviceName, boolean autoRegister) {
        this.serviceName = serviceName;
        this.autoRegister = autoRegister;
    }

    public MessageRegistrationBuilder builder(Class<?> expectedClass) {
        return new MessageRegistrationBuilder(this, serviceName, expectedClass);
    }

    public void add(MessageRegistrationDto messageRegistrationDto) {
        this.classRegistrations.put(messageRegistrationDto.getMessageType(), messageRegistrationDto);
    }

    public void apply() {
        final Map<String, Class<?>> classMappings = new HashMap<>();

        classRegistrations.forEach((k, v) ->
        {
            classMappings.put(k, v.getMessageClass());
        });

        super.setIdClassMapping(classMappings);
    }

    @Override
    public Collection<MessageRegistrationDto> getRegistrations() {
        return classRegistrations.values();
    }

    @Override
    public boolean isAutoRegister() {
        return autoRegister;
    }

    public class MessageRegistrationBuilder {
        private RegistrationAwareClassMapper classMapper;
        private String serviceName;
        private Class<?> expectedClass;
        private List<MessageExchangeDto> exchanges = new ArrayList<>();
        private List<MessageQueueDto> queues = new ArrayList<>();

        public MessageRegistrationBuilder(RegistrationAwareClassMapper classMapper, String serviceName, Class<?> expectedClass) {
            this.classMapper = classMapper;
            this.serviceName = serviceName;
            this.expectedClass = expectedClass;
        }

        public MessageRegistrationBuilder toExchange(String name) {
            return toExchange(name, MessageDirectionType.PRODUCE);
        }

        public MessageRegistrationBuilder toExchange(String name, MessageDirectionType direction) {
            exchanges.add(new MessageExchangeDto(name, direction));
            return this;
        }

        public MessageRegistrationBuilder fromQueue(String name) {
            queues.add(new MessageQueueDto(name));
            return this;
        }

        public MessageRegistrationDto register() {
            final List<MessageRegistrationDto> result = new ArrayList<>();

            final MessageAnnotationProcessor messageAnnotationProcessor = new MessageAnnotationProcessor();
            messageAnnotationProcessor.process((messageType, messageClass) ->
            {
                JsonSchema schema = createSchema(expectedClass);
                result.add(new MessageRegistrationDto(serviceName, messageClass, messageType, "1.0",
                        schema, exchanges, queues));
            }, expectedClass);

            MessageRegistrationDto dto = result.iterator().next();
            classMapper.add(dto);

            return dto;
        }

        private JsonSchema createSchema(Class<?> clazz) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
                return mapper.generateJsonSchema(clazz);
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
