package com.monadiccloud.core.amqp.context.builder;

import com.monadiccloud.core.amqp.consumer.handler.AmqpContextAwareMessageHandler;
import com.monadiccloud.core.amqp.consumer.handler.DelegatingMessageHandler;
import com.monadiccloud.core.amqp.context.*;
import com.monadiccloud.core.amqp.message.MessageConverterFactory;
import com.monadiccloud.core.amqp.message.annotation.stereotypes.MessageStereotype;
import com.monadiccloud.core.amqp.retrypolicy.RetryPolicyFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.AbstractJsonMessageConverter;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.retry.support.RetryTemplate;

import java.io.File;
import java.util.*;

public class AmqpContextBuilder {
    final Map<String, Exchange> exchanges = new HashMap<>();
    final Map<String, Queue> queues = new HashMap<>();
    final Map<String, Binding> bindings = new HashMap<>();
    final Map<String, MessageDescription> descriptions = new HashMap<>();
    final List<MessageListenerContainer> containers = new ArrayList<>();
    final Map<String, ContainerQueueData> containerQueueDataMap = new HashMap<>();
    final List<AmqpContextAware> contextAwares = new ArrayList<>();
    final Map<RequestReplyKey, String> replyToMap = new HashMap<>();

    private MessageDescriptionFactory messageDescriptionFactory = null;
    private ContainerFactory containerFactory = null;

    private ConnectionFactory rabbitConnectionFactory;
    private ApplicationConfiguration applicationConfiguration;
    private String consumerPostfix;

    public AmqpContextBuilder(ConnectionFactory rabbitConnectionFactory, ApplicationConfiguration configuration) {
        this(rabbitConnectionFactory, configuration, Collections.emptyList());
    }

    public AmqpContextBuilder(ConnectionFactory rabbitConnectionFactory, ApplicationConfiguration configuration, File file) {
        this(rabbitConnectionFactory, configuration, new MessageMetaDataReader().read(file));
    }

    public AmqpContextBuilder(ConnectionFactory rabbitConnectionFactory, ApplicationConfiguration configuration,
                              Collection<MessageMetaData> metaDatas) {
        this.rabbitConnectionFactory = rabbitConnectionFactory;
        this.applicationConfiguration = configuration;
        this.consumerPostfix = configuration.getApplicationName() + "." + configuration.getHostName();

        this.messageDescriptionFactory = new MessageDescriptionFactory(configuration, metaDatas);
        this.containerFactory = new ContainerFactory();
    }

    public <P> AmqpContextBuilder produces(Class<P> produceClass) {
        // Create durable exchanage by default
        return produces(produceClass, true);
    }

    public <P> AmqpContextBuilder produces(Class<P> produceClass, boolean durable) {
        MessageDescription<P> produceDescription = messageDescriptionFactory.createDescription(produceClass);
        descriptions.put(produceDescription.getType(), produceDescription);
        MessageExchangeBuilder builder = new MessageExchangeBuilder(this, produceDescription.getExchange(),
                produceDescription.getExchangeType());

        if (durable) {
            builder.durable();
        }
        builder.exchange();
        return this;
    }

    public <C> AmqpContextBuilder consumes(String queueName, boolean durable, AmqpContextAwareMessageHandler handler,
                                           Class<C> messageClass) {
        return consumes(queueName, durable, null, handler, messageClass);
    }

    public <C> AmqpContextBuilder consumes(String queueName, boolean durable, String containerAlias,
                                           AmqpContextAwareMessageHandler handler, Class<C> messageClass) {
        MessageDescription<C> description = messageDescriptionFactory.createDescription(messageClass);
        descriptions.put(description.getType(), description);

        //If the container alias happens to be null, then all queues will get the same 'null' container
        addQueueData(containerAlias, queueName, messageClass, handler);

        MessageBindingBuilder bindingBuilder = new MessageBindingBuilder(this, resolveRoutingKey(description, consumerPostfix));
        bindingBuilder.fromExchange(description.getExchange(), description.getExchangeType()).toQueue(queueName, durable);
        bindingBuilder.bind();

        return this;
    }

    public <P> AmqpContextBuilder producesAndConsumes(Class<P> requestClass, String queueName, boolean durable,
                                                      String containerAlias, AmqpContextAwareMessageHandler handler,
                                                      Class<?>... replyClasses) {
        produces(requestClass);

        MessageDescription<P> requestDescription = messageDescriptionFactory.createDescription(requestClass);
        descriptions.put(requestDescription.getType(), requestDescription);

        for (Class<?> replyClass : replyClasses) {
            MessageDescription replyDescription = messageDescriptionFactory.createDescription(replyClass);
            descriptions.put(replyDescription.getType(), replyDescription);

            addQueueData(containerAlias, queueName, replyClass, handler);

            MessageBindingBuilder replyBindingBuilder = new MessageBindingBuilder(this,
                    resolveRoutingKey(replyDescription.getStereotype(), requestDescription.getRoutingKey(), consumerPostfix));

            Binding replyBinding = replyBindingBuilder.fromExchange(replyDescription.getExchange(), replyDescription.getExchangeType())
                    .toQueue(queueName, durable).bind();

            replyToMap.put(new RequestReplyKey(requestClass, replyClass), replyBinding.getRoutingKey());
        }

        return this;
    }

    public <P> AmqpContextBuilder producesAndConsumes(Class<P> requestClass, String queueName, boolean durable,
                                                      AmqpContextAwareMessageHandler handler,
                                                      Class<?>... replyClasses) {
        return producesAndConsumes(requestClass, queueName, durable, null, handler, replyClasses);
    }

    public AmqpContextBuilder addContextAware(AmqpContextAware contextAware) {
        this.contextAwares.add(contextAware);
        return this;
    }

    public AmqpContext build() {
        RabbitAdmin admin = new RabbitAdmin(rabbitConnectionFactory);

        ClassMapper mapper = createClassMapper();
        AbstractJsonMessageConverter converter = createMessageConverter(mapper);

        RetryTemplate retryTemplate = createRetryTemplate();
        RabbitTemplate rabbitTemplate = createRabbitTemplate(converter, retryTemplate);

        // Create containers for queues based on the containerAlias value on the message.
        // Default behaviour will be to add all queues to a single container
        containerQueueDataMap.forEach((containerAlias, containerData) ->
        {
            SimpleMessageListenerContainer container = containerFactory
                    .createDefaultContainer(consumerPostfix + "-" + containerAlias, rabbitConnectionFactory, converter,
                            containerData.getListener());
            containerData.getQueueNames().forEach(q -> container.addQueues(queues.get(q)));
            containers.add(container);
        });

        AmqpContext context = new AmqpContext(consumerPostfix, admin, rabbitTemplate, converter, exchanges.values(), queues.values(),
                bindings.values(), descriptions.values(), containers, replyToMap);

        // Set the context in anything that has been added as a RabbitContextAware
        contextAwares.forEach(contextAware -> contextAware.setAmqpContext(context));

        return context;
    }

    public AmqpContextBuilder add(Binding binding) {
        bindings.put(Arrays.toString(new String[]{binding.getExchange(), binding.getExchange(), binding.getRoutingKey()}), binding);
        return this;
    }

    public AmqpContextBuilder add(Exchange exchange) {
        exchanges.put(exchange.getName(), exchange);
        return this;
    }

    public AmqpContextBuilder add(Queue queue) {
        queues.put(queue.getName(), queue);
        return this;
    }

    public AmqpContextBuilder add(MessageListenerContainer container) {
        containers.add(container);
        return this;
    }

    private void addQueueData(String containerAlias, String queueName, Class messageClass,
                              AmqpContextAwareMessageHandler handler) {
        //If the container alias happens to be null, then all queues will get the same 'null' container
        ContainerQueueData queueData = containerQueueDataMap.get(containerAlias);
        if (queueData == null) {
            queueData = new ContainerQueueData(containerAlias, queueName);
            contextAwares.add(queueData);
            containerQueueDataMap.put(containerAlias, queueData);
        } else {
            queueData.addQueueName(queueName);
        }
        queueData.addHandler(messageClass, handler);
    }

    private RabbitTemplate createRabbitTemplate(MessageConverter messageConverter, RetryTemplate retryTemplate) {
        RabbitTemplate template = new RabbitTemplate(rabbitConnectionFactory);
        template.setMessageConverter(messageConverter);
        template.setRetryTemplate(retryTemplate);
        return template;
    }

    private RetryTemplate createRetryTemplate() {
        return RetryPolicyFactory.createDefaultTemplateRetry();
    }

    private AbstractJsonMessageConverter createMessageConverter(ClassMapper mapper) {
        return (AbstractJsonMessageConverter) MessageConverterFactory.createDefaultConverter(mapper);
    }

    private ClassMapper createClassMapper() {
        final Map<String, Class<?>> classMappings = new HashMap<>();
        descriptions.forEach((k, v) -> classMappings.put(v.getType(), v.getMessageClass()));

        final DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setIdClassMapping(classMappings);
        return classMapper;
    }

    private <M> String resolveRoutingKey(MessageDescription<M> messageDescription, String consumerPostfix) {
        String routingKey = messageDescription.getRoutingKey();
        if (routingKey == null) {
            routingKey = messageDescription.getType();
        }

        MessageStereotype stereotype = messageDescription.getStereotype();
        return resolveRoutingKey(stereotype, routingKey, consumerPostfix);
    }

    private String resolveRoutingKey(MessageStereotype stereotype, String routingKey, String consumerPostfix) {
        StringBuilder builder = new StringBuilder();
        builder.append(routingKey);

        if (MessageStereotype.REPLY == stereotype || MessageStereotype.ERROR == stereotype) {
            builder.append("." + consumerPostfix);
        }
        return builder.toString();
    }

    private static class ContainerQueueData implements AmqpContextAware {
        private String containerAlias;
        private Set<String> queueNames = new HashSet<>();
        private DelegatingMessageHandler handler = new DelegatingMessageHandler();

        public ContainerQueueData(String containerAlias, String queueName) {
            this.containerAlias = containerAlias;
            this.queueNames.add(queueName);
        }

        public void addHandler(Class messageClass, AmqpContextAwareMessageHandler messageHandler) {
            this.handler.addHandler(messageClass, messageHandler);
        }

        public boolean addQueueName(String s) {
            return queueNames.add(s);
        }

        public String getContainerAlias() {
            return this.containerAlias;
        }

        public Collection<String> getQueueNames() {
            return queueNames;
        }

        public DelegatingMessageHandler getListener() {
            return handler;
        }

        @Override
        public void setAmqpContext(AmqpContext rabbitContext) {
            this.handler.setAmqpContext(rabbitContext);
        }
    }

    public static class MessageQueueBuilder {
        private AmqpContextBuilder contextBuilder;
        private QueueBuilder nativeBuilder;

        public MessageQueueBuilder(AmqpContextBuilder contextBuilder, String name, boolean durable) {
            this.contextBuilder = contextBuilder;
            if (durable) {
                nativeBuilder = QueueBuilder.durable(name);
            } else {
                nativeBuilder = QueueBuilder.nonDurable(name);
            }
        }

        public MessageQueueBuilder exclusive() {
            nativeBuilder.exclusive();
            return this;
        }

        public MessageQueueBuilder autoDelete() {
            nativeBuilder.autoDelete();
            return this;
        }

        public MessageQueueBuilder withArgument(String key, Object value) {
            nativeBuilder.withArgument(key, value);
            return this;
        }

        public MessageQueueBuilder withArguments(Map<String, Object> arguments) {
            nativeBuilder.withArguments(arguments);
            return this;
        }

        public AmqpContextBuilder context() {
            queue();
            return contextBuilder;
        }

        public Queue queue() {
            Queue queue = nativeBuilder.build();
            contextBuilder.add(queue);
            return queue;
        }
    }

    public static class MessageExchangeBuilder {
        private AmqpContextBuilder contextBuilder;
        private ExchangeBuilder nativeBuilder;

        private MessageExchangeBuilder(AmqpContextBuilder builder, String name, MessageExchangeType type) {
            this.contextBuilder = builder;
            this.nativeBuilder = createBuilder(name, type);
        }

        private ExchangeBuilder createBuilder(String name, MessageExchangeType type) {
            switch (type) {
                case TOPIC:
                    return ExchangeBuilder.topicExchange(name);
                case DIRECT:
                    return ExchangeBuilder.directExchange(name);
                case HEADERS:
                    return ExchangeBuilder.headersExchange(name);
                case FANOUT:
                    return ExchangeBuilder.fanoutExchange(name);
            }
            return null;
        }

        public MessageExchangeBuilder autoDelete() {
            nativeBuilder.autoDelete();
            return this;
        }

        public MessageExchangeBuilder durable() {
            nativeBuilder.durable();
            return this;
        }

        public MessageExchangeBuilder withArgument(String key, Object value) {
            nativeBuilder.withArgument(key, value);
            return this;
        }

        public MessageExchangeBuilder withArguments(Map<String, Object> arguments) {
            nativeBuilder.withArguments(arguments);
            return this;
        }

        public MessageExchangeBuilder internal() {
            nativeBuilder.internal();
            return this;
        }

        public MessageExchangeBuilder delayed() {
            nativeBuilder.delayed();
            return this;
        }

        public AmqpContextBuilder context() {
            exchange();
            return contextBuilder;
        }

        public Exchange exchange() {
            Exchange exchange = nativeBuilder.build();
            contextBuilder.add(exchange);
            return exchange;
        }
    }

    public static class MessageBindingBuilder {
        private AmqpContextBuilder contextBuilder;
        private MessageExchangeBuilder exchangeBuilder;
        private MessageQueueBuilder queueBuilder;
        private String bindingBase;

        public MessageBindingBuilder(AmqpContextBuilder builder, String bindingBase) {
            this.contextBuilder = builder;
            this.bindingBase = bindingBase;
        }

        public MessageBindingBuilder fromTopicExchange(String exchangeName) {
            return fromExchange(exchangeName, MessageExchangeType.TOPIC);
        }

        public MessageBindingBuilder fromDirectExchange(String exchangeName) {
            return fromExchange(exchangeName, MessageExchangeType.DIRECT);
        }

        public MessageBindingBuilder fromHeadersExchange(String exchangeName) {
            return fromExchange(exchangeName, MessageExchangeType.HEADERS);
        }

        public MessageBindingBuilder fromFanoutExchange(String exchangeName) {
            return fromExchange(exchangeName, MessageExchangeType.FANOUT);
        }

        public MessageBindingBuilder fromExchange(String exchangeName, MessageExchangeType exchangeType) {
            // Durable by default
            return fromExchange(exchangeName, exchangeType, true);
        }

        public MessageBindingBuilder fromExchange(String exchangeName, MessageExchangeType exchangeType, boolean durable) {
            exchangeBuilder = new MessageExchangeBuilder(contextBuilder, exchangeName, exchangeType);
            if (durable) {
                exchangeBuilder.durable();
            }
            return this;
        }

        public MessageBindingBuilder toQueue(String queueName, boolean durable) {
            queueBuilder = new MessageQueueBuilder(contextBuilder, queueName, durable);
            return this;
        }

        public MessageBindingBuilder toDurableQueue(String queueName) {
            return toQueue(queueName, true);
        }

        public MessageBindingBuilder toNonDurableQueue(String queueName) {
            return toQueue(queueName, false);
        }

        public MessageExchangeBuilder exchange() {
            return exchangeBuilder;
        }

        public MessageQueueBuilder queue() {
            return queueBuilder;
        }

        public AmqpContextBuilder context() {
            bind();
            return contextBuilder;
        }

        public Binding bind() {
            return bind(queueBuilder.queue(), exchangeBuilder.exchange());
        }

        private Binding bind(Queue queue, Exchange exchange) {
            Binding binding = BindingBuilder.bind(queue).to(exchange).with(bindingBase).noargs();
            contextBuilder.add(binding);
            return binding;
        }

    }
}
