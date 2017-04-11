package com.monadiccloud.core.amqp.message;

public interface ErrorContainer {
    String getCode();

    void setCode(String code);

    String getMessage();

    void setMessage(String detail);
}
