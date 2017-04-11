package com.monadiccloud.core.amqp.message;

import java.util.Date;

public interface TimestampAware {
    Date getTimestamp();

    void setTimestamp(Date timestamp);
}
