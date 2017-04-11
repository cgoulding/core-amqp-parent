package com.monadiccloud.core.amqp.context.builder;

import com.monadiccloud.core.amqp.connectors.RabbitMqConnectionFactory;
import com.monadiccloud.core.amqp.context.AmqpContext;
import com.monadiccloud.core.amqp.context.ApplicationConfiguration;
import com.monadiccloud.core.amqp.context.ApplicationConfigurationFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;

import java.util.Arrays;
import java.util.Collection;

public class RabbitContextBuilderTest {
    @Test
    public void testConsumeRequest() {
        RabbitContextBuilder builder = new RabbitContextBuilder(new RabbitMqConnectionFactory(), appConfig("appXXX"),
                Arrays.asList(new MessageMetaData("test.message.request", "asdf", null, "routing.base")));
        builder.consumes("queue1", false, new Object(), TestRequestMessage.class);
        AmqpContext context = builder.build();

        Assert.assertNotNull(context.getAdmin());
        Assert.assertNotNull(context.getRabbitTemplate());
        Assert.assertNotNull(context.getRabbitTemplate().getMessageConverter());
        Assert.assertNotNull(context.getContextUuid());

        Collection<Exchange> exchanges = context.getExchanges();
        Collection<Queue> queues = context.getQueues();
        Collection<Binding> bindings = context.getBindings();

        Assert.assertEquals(1, exchanges.size());
        Assert.assertEquals(1, queues.size());
        Assert.assertEquals(1, bindings.size());

        Binding binding = bindings.stream().findAny().get();
        Assert.assertEquals("routing.base", binding.getRoutingKey());
    }

    @Test
    public void testProduceRequest() {
        RabbitContextBuilder builder = new RabbitContextBuilder(new RabbitMqConnectionFactory(), appConfig("appXXX"),
                Arrays.asList(new MessageMetaData("test.message.request", "asdf", null, "routing.base")));
        builder.produces(TestRequestMessage.class);
        AmqpContext context = builder.build();

        Assert.assertNotNull(context.getAdmin());
        Assert.assertNotNull(context.getRabbitTemplate());
        Assert.assertNotNull(context.getRabbitTemplate().getMessageConverter());
        Assert.assertNotNull(context.getContextUuid());

        Collection<Exchange> exchanges = context.getExchanges();
        Collection<Queue> queues = context.getQueues();
        Collection<Binding> bindings = context.getBindings();

        Assert.assertEquals(1, exchanges.size());
        Assert.assertEquals(0, queues.size());
        Assert.assertEquals(0, bindings.size());
    }

    @Test
    public void testConsumeReply() {
        RabbitContextBuilder builder = new RabbitContextBuilder(new RabbitMqConnectionFactory(), appConfig("appXXX"),
                Arrays.asList(new MessageMetaData("test.message.reply", "asdf", null, "routing.base")));
        builder.consumes("queue1", false, new Object(), TestReplyMessage.class);
        AmqpContext context = builder.build();

        Assert.assertNotNull(context.getAdmin());
        Assert.assertNotNull(context.getRabbitTemplate());
        Assert.assertNotNull(context.getRabbitTemplate().getMessageConverter());
        Assert.assertNotNull(context.getContextUuid());

        Collection<Exchange> exchanges = context.getExchanges();
        Collection<Queue> queues = context.getQueues();
        Collection<Binding> bindings = context.getBindings();

        Assert.assertEquals(1, exchanges.size());
        Assert.assertEquals(1, queues.size());
        Assert.assertEquals(1, bindings.size());

        Binding binding = bindings.stream().findAny().get();
        Assert.assertTrue(binding.getRoutingKey().startsWith("routing.base."));
        Assert.assertTrue(binding.getRoutingKey().contains("appXXX"));
    }

    @Test
    public void testProduceReply() {
        RabbitContextBuilder builder = new RabbitContextBuilder(new RabbitMqConnectionFactory(), appConfig("appXXX"),
                Arrays.asList(new MessageMetaData("test.message.reply", "asdf", null, "routing.base")));
        builder.produces(TestReplyMessage.class);
        AmqpContext context = builder.build();

        Assert.assertNotNull(context.getAdmin());
        Assert.assertNotNull(context.getRabbitTemplate());
        Assert.assertNotNull(context.getRabbitTemplate().getMessageConverter());
        Assert.assertNotNull(context.getContextUuid());

        Collection<Exchange> exchanges = context.getExchanges();
        Collection<Queue> queues = context.getQueues();
        Collection<Binding> bindings = context.getBindings();

        Assert.assertEquals(1, exchanges.size());
        Assert.assertEquals(0, queues.size());
        Assert.assertEquals(0, bindings.size());
    }

    @Test
    public void testRequestAndReply() {
        RabbitContextBuilder builder = new RabbitContextBuilder(new RabbitMqConnectionFactory(), appConfig("appXXX"),
                Arrays.asList(new MessageMetaData("test.message.request", "asdf", null, "routing.base"),
                        new MessageMetaData("test.message.reply", "asdf", null, "routing.base")));

        builder.requestsAndReplies(TestRequestMessage.class, "requestReply", false, new Object(), TestReplyMessage.class);
        AmqpContext context = builder.build();

        Collection<Exchange> exchanges = context.getExchanges();
        Collection<Queue> queues = context.getQueues();
        Collection<Binding> bindings = context.getBindings();

        Assert.assertEquals(1, exchanges.size());
        Assert.assertEquals(1, queues.size());
        Assert.assertEquals(1, bindings.size());
    }

    private ApplicationConfiguration appConfig(String name) {
        return ApplicationConfigurationFactory.getInstance().createApplicationConfiguration(name);
    }
}
