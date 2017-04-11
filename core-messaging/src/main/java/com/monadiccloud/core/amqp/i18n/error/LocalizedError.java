package com.monadiccloud.core.amqp.i18n.error;

public class LocalizedError {
    private String messageCode;
    private String message;

    public LocalizedError(String messageCode, String message) {
        this.messageCode = messageCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public LocalizedError setMessageCode(String messageCode) {
        this.messageCode = messageCode;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public LocalizedError setMessage(String message) {
        this.message = message;
        return this;
    }
}
