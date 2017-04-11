package com.monadiccloud.core.amqp.registration.notifier.model;

import java.util.ArrayList;
import java.util.List;

public class MessageExchangeDto {
    private String name;
    private MessageDirectionType direction;
    private List<BindingDataDto> bindings;

    public MessageExchangeDto(String name, MessageDirectionType direction) {
        this(name, direction, new ArrayList<>());
    }

    public MessageExchangeDto(String name, MessageDirectionType direction, List<BindingDataDto> bindings) {
        this.name = name;
        this.direction = direction;
        this.bindings = bindings;
    }

    public String getName() {
        return name;
    }

    public MessageDirectionType getDirection() {
        return direction;
    }

    public List<BindingDataDto> getBindings() {
        return bindings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MessageExchangeDto that = (MessageExchangeDto) o;

        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;
        if (direction != that.direction)
            return false;
        return bindings != null ? bindings.equals(that.bindings) : that.bindings == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        result = 31 * result + (bindings != null ? bindings.hashCode() : 0);
        return result;
    }
}
