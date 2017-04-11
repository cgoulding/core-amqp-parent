package com.monadiccloud.core.amqp.connectors;

import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;

import javax.annotation.PostConstruct;

public final class RabbitMqConnectionFactory extends CachingConnectionFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqConnectionFactory.class);

    private String host;
    private String password;
    private String username;
    private int port;
    private String virtualHost;
    private Integer heartbeat;

    public RabbitMqConnectionFactory() {
        super(new ConnectionFactory());
    }

    public RabbitMqConnectionFactory(String host, String password, int port, String virtualHost, String username) {
        super(new ConnectionFactory());

        this.host = host;
        this.password = password;
        this.port = port;
        this.virtualHost = virtualHost;
        this.username = username;

        this.init();
    }

    @PostConstruct
    protected void init() {
        setHost(this.host);
        setPort(this.port);
        setUsername(this.username);
        setPassword(this.password);
        setVirtualHost(this.virtualHost);
        setRequestedHeartBeat(this.heartbeat);

        addConnectionListener(new ConnectionListener() {
            @Override
            public void onCreate(Connection connection) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Created AMQP connection");
                }
            }

            @Override
            public void onClose(Connection connection) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Closing AMQP connection");
                }
            }
        });
    }
}
