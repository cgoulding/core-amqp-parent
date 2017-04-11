package com.monadiccloud.core.amqp.consumer.error;

import com.monadiccloud.core.amqp.exceptions.RabbitMQException;
import com.monadiccloud.core.amqp.i18n.error.LocalizedError;
import com.monadiccloud.core.amqp.message.ErrorContainer;
import com.monadiccloud.core.amqp.message.ErrorsAware;
import com.monadiccloud.core.amqp.message.MessagePropertiesAware;
import com.monadiccloud.core.amqp.message.MessagePropertiesContainer;
import com.monadiccloud.core.amqp.retrypolicy.exception.ErrorResponseException;
import com.monadiccloud.core.amqp.retrypolicy.exception.ResponseMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class DefaultErrorTransformer<
        ErrorMessage extends ErrorContainer,
        ErrorResponseMessage extends MessagePropertiesAware<? extends MessagePropertiesContainer> & ErrorsAware<ErrorMessage>
        >
        implements ErrorTransformer<MessagePropertiesAware<?>> {
    public static final String ERROR_BINDING_TEMPLATE = "${handler.routingKey}.error.${request.replyTo}";
    private static final Logger log = LoggerFactory.getLogger(DefaultErrorTransformer.class);
    protected Supplier<ErrorResponseMessage> errorMessageSupplier;
    protected Supplier<ErrorMessage> errorSupplier;
    protected String responseExchange;
    protected String replyTo;

    public DefaultErrorTransformer(String responseExchange,
                                   String replyTo,
                                   Supplier<ErrorResponseMessage> errorMessageSupplier,
                                   Supplier<ErrorMessage> errorSupplier) {
        this.errorMessageSupplier = errorMessageSupplier;
        this.errorSupplier = errorSupplier;
        this.responseExchange = responseExchange;
        this.replyTo = replyTo;
    }

    @Override
    public Exception transform(Exception e, ErrorContext<MessagePropertiesAware<?>> context) {
        if (e instanceof ResponseMessageException) {
            return e;
        }

        return createResponseMessageException(e.getMessage(), e, context);
    }

    protected Exception createResponseMessageException(String errorText, Exception e, ErrorContext<MessagePropertiesAware<?>> context) {
        LocalizedError error = new LocalizedError("XYZ", errorText);
        return createResponseMessageException(asList(error), e, context);
    }

    protected Exception createResponseMessageException(List<LocalizedError> errors, Exception e, ErrorContext<MessagePropertiesAware<?>> context) {
        try {
            MessagePropertiesAware<?> requestMessage = context.getRequestMessage();
            validate(requestMessage);

            String routingKey = createRoutingKey(context);

            ErrorResponseMessage message = errorMessageSupplier.get();
            populateErrorMessage(errors, requestMessage, message);

            return new ErrorResponseException(e, responseExchange, routingKey, message);
        } catch (Exception internalError) {
            String msg = "Response Exception";
            log.error(msg, internalError);
            return e;
        }
    }

    protected void validate(MessagePropertiesAware<?> requestMessage) throws RabbitMQException {
        MessagePropertiesContainer messageProperties = requestMessage.getMessageProperties();
        if (messageProperties == null) {
            throw new RabbitMQException("Missing property messageProperties");
        }
        if (isEmpty(messageProperties.getReplyTo())) {
            throw new RabbitMQException("Missing property replyTo");
        }
        if (isEmpty(messageProperties.getCorrelationId())) {
            throw new RabbitMQException("Missing property correlationId");
        }
    }

    protected String createRoutingKey(ErrorContext<MessagePropertiesAware<?>> context) throws RabbitMQException {
        MessagePropertiesAware<?> requestMessage = context.getRequestMessage();
        return ERROR_BINDING_TEMPLATE
                .replace("${handler.routingKey}", context.getErrorRoutingKeyPrefix())
                .replace("${request.replyTo}", requestMessage.getMessageProperties().getReplyTo());
    }

    protected void populateErrorMessage(List<LocalizedError> errors, MessagePropertiesAware<?> requestMessage, ErrorResponseMessage message) {
        MessagePropertiesContainer properties = message.getMessageProperties();
        properties.setCorrelationId(requestMessage.getMessageProperties().getCorrelationId());
        properties.setReplyTo(replyTo);
        properties.setTimestamp(new Date());

        List<ErrorMessage> errorMessages = new ArrayList<>();
        for (LocalizedError localizedError : errors) {
            ErrorMessage errorMessage = errorSupplier.get();
            populateErrorDetails(errorMessage, localizedError);
            errorMessages.add(errorMessage);
        }

        message.setErrors(errorMessages);
    }

    protected void populateErrorDetails(ErrorMessage errorMessage, LocalizedError localizedError) {
        errorMessage.setCode(localizedError.getMessageCode());
        errorMessage.setMessage(localizedError.getMessage());
    }
}
