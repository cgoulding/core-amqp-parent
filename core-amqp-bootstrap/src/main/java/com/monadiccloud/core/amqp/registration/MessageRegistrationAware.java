package com.monadiccloud.core.amqp.registration;

import com.monadiccloud.core.amqp.registration.notifier.model.MessageRegistrationDto;

import java.util.Collection;

public interface MessageRegistrationAware {
    Collection<MessageRegistrationDto> getRegistrations();

    boolean isAutoRegister();
}
