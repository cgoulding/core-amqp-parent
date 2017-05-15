package com.monadiccloud.core.amqp.message.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageRequestProperties {

    String replyToProperty() default "replyTo";

}
