package com.monadiccloud.core.amqp;

import com.monadiccloud.core.amqp.message.annotation.Message;

import java.lang.annotation.Annotation;
import java.util.List;

public class MessageAnnotationProcessor {
    public void process(final MessageAnnotationProcessorCallback callback, final List<Class<?>> classes) {
        for (final Class aClass : classes) {
            process(callback, aClass);
        }
    }

    public void process(final MessageAnnotationProcessorCallback callback, final Class<?> aClass) {
        Annotation annotation = aClass.getAnnotation(Message.class);

        if (annotation != null) {
            Message messageAnnotation = (Message) annotation;
            callback.found(messageAnnotation.value(), aClass);
        }
    }
}
