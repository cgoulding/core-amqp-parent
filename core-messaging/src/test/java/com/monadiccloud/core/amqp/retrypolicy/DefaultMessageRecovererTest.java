package com.monadiccloud.core.amqp.retrypolicy;

import com.monadiccloud.core.amqp.retrypolicy.exception.ResponseMessageException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.exception.ListenerExecutionFailedException;

import static org.mockito.Mockito.mock;

public class DefaultMessageRecovererTest {
    DefaultMessageRecoverer recoverer;
    RabbitTemplate rabbitTemplate;
    Message message;

    @Before
    public void setUp() throws Exception {
        rabbitTemplate = mock(RabbitTemplate.class);
        message = new Message(new byte[0], new MessageProperties());

        recoverer = new DefaultMessageRecoverer(rabbitTemplate);
    }

    @Test
    public void recover_ResponseMessageException() throws Exception {
        Exception cause = new Exception("Test error");
        Exception e = new ResponseMessageException(cause, "test-exchange", "test-case", "test-body");

        recoverer.recover(message, e);

        Mockito
                .verify(rabbitTemplate)
                .convertAndSend("test-exchange", "test-case", "test-body");
    }

    @Test
    public void recover_RuntimeException() throws Exception {
        Exception e = new RuntimeException("Test error");

        recoverer.recover(message, e);

        Mockito.verifyZeroInteractions(rabbitTemplate);
    }

    @Test
    public void recover_wrappedException() throws Exception {
        Exception cause = new Exception("Test error");
        Exception e = new ListenerExecutionFailedException(
                "Wrapper",
                new ResponseMessageException(cause, "test-exchange", "test-case", "test-body")
        );

        recoverer.recover(message, e);

        Mockito
                .verify(rabbitTemplate)
                .convertAndSend("test-exchange", "test-case", "test-body");
    }
}