package com.monadiccloud.core.amqp.context;

public class ApplicationConfigurationContext {
    private static final ThreadLocal<ApplicationConfiguration> CURRENT = new ThreadLocal<>();

    public static void setCurrent(String applicationName) {
        ApplicationConfiguration applicationConfiguration =
                ApplicationConfigurationFactory.getInstance().createApplicationConfiguration(applicationName);
        setCurrent(applicationConfiguration);
    }

    public static ApplicationConfiguration getCurrent() {
        return CURRENT.get();
    }

    public static void setCurrent(ApplicationConfiguration configuration) {
        CURRENT.set(configuration);
    }
}
