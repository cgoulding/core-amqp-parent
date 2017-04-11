package com.monadiccloud.core.amqp.retrypolicy.exception;

public class ResponseMessageException extends RuntimeException {
    private static final long serialVersionUID = -5837101839767033032L;
    private static final int DEFAULT_RETRY_COUNT = 0;

    private final int maxRetryCount;
    private final ResponseDetails responseDetails;

    public ResponseMessageException(Throwable cause, String exchange, String routingKey, Object body) {
        this(cause, DEFAULT_RETRY_COUNT, exchange, routingKey, body);
    }

    public ResponseMessageException(Throwable cause, int maxRetryCount, String exchange, String routingKey, Object body) {
        this(cause, maxRetryCount, new ResponseDetails(exchange, routingKey, body));
    }

    public ResponseMessageException(Throwable cause, ResponseDetails responseDetails) {
        this(cause, DEFAULT_RETRY_COUNT, responseDetails);
    }

    public ResponseMessageException(Throwable cause, int maxRetryCount, ResponseDetails responseDetails) {
        super(cause);
        this.maxRetryCount = maxRetryCount;
        this.responseDetails = responseDetails;
    }

    @Override
    public String toString() {
        return "ResponseMessageException{" +
                "responseDetails=" + responseDetails +
                ", cause=" + getCause() +
                '}';
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public ResponseDetails getResponseDetails() {
        return responseDetails;
    }
}
