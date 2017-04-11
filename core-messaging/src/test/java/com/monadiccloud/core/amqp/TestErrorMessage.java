package com.monadiccloud.core.amqp;

import com.monadiccloud.core.amqp.message.ErrorsAware;
import com.monadiccloud.core.amqp.message.MessagePropertiesAware;

import java.util.List;

public class TestErrorMessage implements MessagePropertiesAware<TestMessageProperties>, ErrorsAware<Error> {
    private TestMessageProperties messageProperties = new TestMessageProperties();
    private List<Error> errors;

    public TestMessageProperties getMessageProperties() {
        return messageProperties;
    }

    public void setMessageProperties(TestMessageProperties messageProperties) {
        this.messageProperties = messageProperties;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

}
