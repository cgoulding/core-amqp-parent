package com.monadiccloud.core.amqp.retrypolicy;

import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;

public class RootCauseRetryPolicy extends RetryPolicyDelegate {
    public RootCauseRetryPolicy(RetryPolicy delegate) {
        super(delegate);
    }

    @Override
    public void registerThrowable(RetryContext context, Throwable throwable) {
        Throwable cause = throwable.getCause();
        if (throwable == cause) {
            throw new IllegalArgumentException("Exception uses references itself as a root cause", throwable);
        }
        super.registerThrowable(context, cause);
    }
}
