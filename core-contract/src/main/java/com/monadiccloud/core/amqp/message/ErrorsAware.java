package com.monadiccloud.core.amqp.message;

import java.util.List;

public interface ErrorsAware<T extends ErrorContainer> {
    List<T> getErrors();

    void setErrors(List<T> errorMessages);
}
