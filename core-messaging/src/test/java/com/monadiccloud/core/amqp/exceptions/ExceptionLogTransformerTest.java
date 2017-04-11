package com.monadiccloud.core.amqp.exceptions;

import com.monadiccloud.core.amqp.retrypolicy.exception.ErrorResponseException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.rabbit.listener.exception.ListenerExecutionFailedException;

import static org.junit.Assert.assertSame;

public class ExceptionLogTransformerTest {
    ExceptionLogTransformer transformer;

    @Before
    public void setUp() throws Exception {
        transformer = new ExceptionLogTransformer();
    }

    @Test
    public void transform_commonException() throws Exception {
        RuntimeException e = new RuntimeException();

        assertSame(e, transformer.transform(e));
    }

    @Test
    public void transform_listenerException() throws Exception {
        RuntimeException cause = new RuntimeException();
        ListenerExecutionFailedException e = new ListenerExecutionFailedException("", cause);

        assertSame(cause, transformer.transform(e));
    }

    @Test
    public void transform_errorResponseException() throws Exception {
        RuntimeException cause = new RuntimeException();
        ErrorResponseException e = new ErrorResponseException(cause, "exchange", "key", "body");

        assertSame(cause, transformer.transform(e));
    }

    @Test
    public void transform_listenerAndErrorResponseException() throws Exception {
        RuntimeException cause = new RuntimeException();
        ListenerExecutionFailedException e = new ListenerExecutionFailedException(
                "",
                new ErrorResponseException(cause, "exchange", "key", "body")
        );

        assertSame(cause, transformer.transform(e));
    }
}