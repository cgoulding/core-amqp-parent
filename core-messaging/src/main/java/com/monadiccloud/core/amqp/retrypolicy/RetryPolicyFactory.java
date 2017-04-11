package com.monadiccloud.core.amqp.retrypolicy;

import org.springframework.amqp.rabbit.listener.exception.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

public class RetryPolicyFactory {
    private static final int MAX_ATTEMPTS = 10;
    private static final int INITIAL_INTERVAL = 100;
    private static final double MULTIPLIER = 2.0;
    private static final int MAX_INTERVAL = 50000;

    public static RetryTemplate createDefaultTemplateRetry() {
        final RetryTemplate retryTemplate = new RetryTemplate();

        final ExponentialBackOffPolicy backOffPolicy =
                new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(INITIAL_INTERVAL);
        backOffPolicy.setMultiplier(MULTIPLIER);
        backOffPolicy.setMaxInterval(MAX_INTERVAL);

        final SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(MAX_ATTEMPTS);


        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    public static RetryOperationsInterceptor createListenerRetryPolicy() {
        final RetryPolicy retryPolicy =
                RetryPolicyFactory.createClassifierRetryPolicy();

        final RootCauseRetryPolicy rootCauseRetryPolicy = new RootCauseRetryPolicy(retryPolicy);

        final RetryTemplate retryTemplate = new RetryTemplate();
        final BackOffPolicy backoffPolicy = new ExponentialBackOffPolicy();
        retryTemplate.setBackOffPolicy(backoffPolicy);
        retryTemplate.setRetryPolicy(rootCauseRetryPolicy);

        final RetryOperationsInterceptor interceptor = new RetryOperationsInterceptor();
        interceptor.setRetryOperations(retryTemplate);

        return interceptor;
    }

    public static RetryPolicy createClassifierRetryPolicy() {
        final ExceptionClassifierRetryPolicy exceptionClassifierRetryPolicy =
                new ExceptionClassifierRetryPolicy();

        final Map<Class<? extends Throwable>, RetryPolicy> policyMap = new HashMap<>();

        policyMap.put(ClassNotFoundException.class, new NeverRetryPolicy());

        // Default max attempts is 3. After the third attempt, the message is logged.
        policyMap.put(Exception.class, new SimpleRetryPolicy());

        policyMap.put(ListenerExecutionFailedException.class, new SimpleRetryPolicy());

        policyMap.put(MessageConversionException.class, new NeverRetryPolicy());

        exceptionClassifierRetryPolicy.setPolicyMap(policyMap);

        return exceptionClassifierRetryPolicy;
    }
}
