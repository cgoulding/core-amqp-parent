package com.monadiccloud.core.amqp.message.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Message {
    /**
     * This is the message Id, for example the __TypeId__ used for SpringAMQP
     *
     * @return the messageId
     */
    String value();

    /**
     * This is the message version
     *
     * @return the message version
     */
    String version();

    /**
     * Content type
     *
     * @return
     */
    MessageContentType content() default MessageContentType.CLEAR;
}
