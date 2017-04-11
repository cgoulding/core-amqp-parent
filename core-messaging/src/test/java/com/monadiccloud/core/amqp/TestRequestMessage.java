package com.monadiccloud.core.amqp;

import com.monadiccloud.core.amqp.message.MessagePropertiesAware;

public class TestRequestMessage implements MessagePropertiesAware<TestMessageProperties> {
    private TestMessageProperties messageProperties = new TestMessageProperties();

    public TestMessageProperties getMessageProperties() {
        return messageProperties;
    }

    public void setMessageProperties(TestMessageProperties messageProperties) {
        this.messageProperties = messageProperties;
    }
}
