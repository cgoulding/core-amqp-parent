package com.monadiccloud.core.amqp.registration.notifier.service;

import com.monadiccloud.core.amqp.registration.notifier.model.MessageRegistrationDto;

public interface RegistrationNotifierService {
    void notify(MessageRegistrationDto entry);

    void withdraw(MessageRegistrationDto entry);
}
