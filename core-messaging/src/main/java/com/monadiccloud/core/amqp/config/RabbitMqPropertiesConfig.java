package com.monadiccloud.core.amqp.config;

public interface RabbitMqPropertiesConfig {
    String rabbitHostname();

    String secondaryHostnames();

    Integer rabbitPort();

    String rabbitPassword();

    String rabbitUsername();

    String rabbitVirtualHost();

    int rabbitRequestedHeartbeat();

    String dataCenter();

    String applicationName();

}
