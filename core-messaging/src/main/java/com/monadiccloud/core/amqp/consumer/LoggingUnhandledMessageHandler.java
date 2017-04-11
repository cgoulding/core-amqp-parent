package com.monadiccloud.core.amqp.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingUnhandledMessageHandler implements UnhandledMessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingUnhandledMessageHandler.class);

    private String consumerName = null;

    public LoggingUnhandledMessageHandler(final String consumerName) {
        super();

        if (consumerName == null) {
            throw new IllegalArgumentException("The consumer name is null.");
        }

        this.consumerName = consumerName;
    }

    @Override
    public void unhandledMessage(byte[] payload) {
        try {
            final String message = new String(payload);

            Object[] lparams = {this.consumerName, message};
            LOGGER.error("Unhandled message", lparams);
        } catch (Exception exception) {
            Object[] lparams = {this.consumerName, payload};
            LOGGER.error("Unhandled message", lparams, exception);
        }
    }
}