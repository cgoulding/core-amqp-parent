package com.monadiccloud.core.amqp.connectors;

import com.monadiccloud.core.amqp.config.RabbitMqPropertiesConfig;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;

import javax.annotation.PostConstruct;

public final class RabbitMqCachingConnectionFactory extends CachingConnectionFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqCachingConnectionFactory.class);

    private String primaryHost;
    private String secondaryAddresses;
    private String password;
    private String username;
    private Integer port;
    private String virtualHost;
    private Integer heartbeat;

    public RabbitMqCachingConnectionFactory(ConnectionFactory connectionFactory,
                                            RabbitMqPropertiesConfig configuration) {
        super(connectionFactory);

        this.primaryHost = configuration.rabbitHostname();
        this.password = configuration.rabbitPassword();
        this.username = configuration.rabbitUsername();
        this.port = configuration.rabbitPort();
        this.virtualHost = configuration.rabbitVirtualHost();
        this.secondaryAddresses = configuration.secondaryHostnames();
        this.heartbeat = configuration.rabbitRequestedHeartbeat();
        this.init();
    }

    @PostConstruct
    protected void init() {
        String primaryAddress = this.primaryHost + ":" + this.port;
        String addresses = (secondaryAddresses != null) ? primaryAddress + "," + secondaryAddresses : primaryAddress;

        setAddresses(addresses);
        setPassword(this.password);
        setUsername(this.username);
        setVirtualHost(this.virtualHost);
        setRequestedHeartBeat(this.heartbeat);

        addConnectionListener(new ConnectionListener() {
            @Override
            public void onCreate(Connection connection) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Creating AMQP connection factory");
                }
            }

            @Override
            public void onClose(Connection connection) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Closing AMQP connection factory");
                }
            }
        });
    }
}
