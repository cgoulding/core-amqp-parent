package com.monadiccloud.core.amqp.retrypolicy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.config.StatefulRetryOperationsInterceptorFactoryBean;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.interceptor.StatefulRetryOperationsInterceptor;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.retry.support.RetryTemplate;

public class DefaultRetryPolicyAdvice implements MethodInterceptor {
    protected final StatefulRetryOperationsInterceptor delegate;

    public DefaultRetryPolicyAdvice(MessageRecoverer messageRecoverer) {
        this(messageRecoverer, new DefaultRetryPolicy());
    }

    public DefaultRetryPolicyAdvice(MessageRecoverer messageRecoverer, RetryPolicy retryPolicy) {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(createBackOffPolicy());
        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.registerListener(new RetryErrorListener());

        StatefulRetryOperationsInterceptorFactoryBean factory = new StatefulRetryOperationsInterceptorFactoryBean();
        factory.setRetryOperations(retryTemplate);
        factory.setMessageRecoverer(messageRecoverer);

        this.delegate = factory.getObject();
    }

    protected ExponentialBackOffPolicy createBackOffPolicy() {
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setMultiplier(5.0D);
        return backOffPolicy;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return delegate.invoke(invocation);
    }

    protected static class RetryErrorListener extends RetryListenerSupport {
        private static final Logger LOGGER = LoggerFactory.getLogger(RetryErrorListener.class);

        @Override
        public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable cause) {
            LOGGER.error("Error on retry count" + context.getRetryCount(), cause);
        }
    }
}
