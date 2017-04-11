package com.monadiccloud.core.amqp.message;

public interface ReplyToAware {
    String getReplyTo();

    void setReplyTo(String replyTo);
}
