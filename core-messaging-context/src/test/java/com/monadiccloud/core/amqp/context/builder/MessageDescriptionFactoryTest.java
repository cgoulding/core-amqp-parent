package com.monadiccloud.core.amqp.context.builder;

import com.monadiccloud.core.amqp.context.ApplicationConfiguration;
import com.monadiccloud.core.amqp.context.MessageDescription;
import com.monadiccloud.core.amqp.message.annotation.MessageContentType;
import com.monadiccloud.core.amqp.message.annotation.stereotypes.MessageStereotype;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class MessageDescriptionFactoryTest {
    @Test
    public void testCreate() {
        MessageDescriptionFactory factory = new MessageDescriptionFactory(new ApplicationConfiguration(null, null, null),
                Arrays.asList(new MessageMetaData("test.message.request", "exchange.test", "TOPIC", "routing.base")));
        MessageDescription description = factory.createDescription(TestRequestMessage.class);

        Assert.assertEquals("test.message.request", description.getType());
        Assert.assertEquals(TestRequestMessage.class, description.getMessageClass());

        Assert.assertEquals("exchange.test", description.getExchange());
        Assert.assertEquals(MessageExchangeType.TOPIC, description.getExchangeType());

        Assert.assertEquals("routing.base", description.getRoutingKey());
        Assert.assertEquals(MessageStereotype.REQUEST, description.getStereotype());
        Assert.assertEquals(MessageContentType.CLEAR, description.getContentType());
    }

    @Test
    public void testFlavour() {
        MessageDescriptionFactory factory = new MessageDescriptionFactory(
                new ApplicationConfiguration("appName", "appUuid", "appHostName"),
                Arrays.asList(new MessageMetaData("test.message.request", "exchange.test.{providerId}.something", "TOPIC", "routing.base")));
        MessageDescription description = factory.createDescription(TestRequestMessage.class);
        Assert.assertEquals("exchange.test.appName.something", description.getExchange());
    }
}
