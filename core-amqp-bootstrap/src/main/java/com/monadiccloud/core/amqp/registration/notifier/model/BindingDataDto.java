package com.monadiccloud.core.amqp.registration.notifier.model;

public class BindingDataDto {
    private String queueName;
    private String routingKey;

    public BindingDataDto(String queueName, String routingKey) {
        this.queueName = queueName;
        this.routingKey = routingKey;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        BindingDataDto that = (BindingDataDto) o;

        if (queueName != null ? !queueName.equals(that.queueName) : that.queueName != null)
            return false;
        return routingKey != null ? routingKey.equals(that.routingKey) : that.routingKey == null;

    }

    @Override
    public int hashCode() {
        int result = queueName != null ? queueName.hashCode() : 0;
        result = 31 * result + (routingKey != null ? routingKey.hashCode() : 0);
        return result;
    }
}
