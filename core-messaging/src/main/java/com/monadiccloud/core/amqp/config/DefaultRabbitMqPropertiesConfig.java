package com.monadiccloud.core.amqp.config;

import org.springframework.core.env.Environment;

/**
 * @author Connor Goulding
 */
public class DefaultRabbitMqPropertiesConfig implements RabbitMqPropertiesConfig {

    private final Environment environment;

    public DefaultRabbitMqPropertiesConfig(Environment environment) {
        this.environment = environment;
    }

    public String rabbitHostname() {
        return environment.getRequiredProperty("monadiccloud.amqp.rabbitHostname");
    }

    public String secondaryHostnames() {
        return environment.getProperty("monadiccloud.secondaries.amqp.rabbitHostname", "");
    }

    public Integer rabbitPort() {
        return environment.getProperty("monadiccloud.amqp.rabbitPort", Integer.class, 5672);
    }

    public String rabbitPassword() {
        return environment.getProperty("monadiccloud.amqp.rabbitPassword", "");
    }

    public String rabbitUsername() {
        return environment.getProperty("monadiccloud.amqp.rabbitUsername", "");
    }

    public String rabbitVirtualHost() {
        return environment.getRequiredProperty("monadiccloud.amqp.rabbitVirtualHost");
    }

    public Integer rabbitRequestedHeartbeat() {
        return environment.getProperty("monadiccloud.amqp.rabbitRequestedHeartbeat", Integer.class, 0);
    }

    public String applicationName() {
        return environment.getProperty("application.name", "");
    }
}
