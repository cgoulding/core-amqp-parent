package com.monadiccloud.core.amqp.message.annotation.stereotypes;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageEvent {
    MessageStereotype stereotype() default MessageStereotype.EVENT;
}
