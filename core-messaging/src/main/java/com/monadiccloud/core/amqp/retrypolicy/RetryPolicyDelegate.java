package com.monadiccloud.core.amqp.retrypolicy;

import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;

public abstract class RetryPolicyDelegate implements RetryPolicy {
    private RetryPolicy delegate;

    public RetryPolicyDelegate(RetryPolicy delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean canRetry(RetryContext context) {
        return delegate.canRetry(context);
    }

    @Override
    public RetryContext open(RetryContext parent) {
        return delegate.open(parent);
    }

    @Override
    public void close(RetryContext context) {
        delegate.close(context);
    }

    @Override
    public void registerThrowable(RetryContext context, Throwable throwable) {
        delegate.registerThrowable(context, throwable);
    }
}
