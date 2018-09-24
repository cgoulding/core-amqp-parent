package com.monadiccloud.core.amqp.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Component
public class AmqpContextListener implements ApplicationListener<ApplicationContextEvent> {
    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        if (event instanceof ContextClosedEvent || event instanceof ContextStoppedEvent || event instanceof ContextRefreshedEvent) {
            ApplicationContext context = event.getApplicationContext();

            AmqpContext rabbitContext = context.getBean(AmqpContext.class);
            if (rabbitContext == null) {
                return;
            }

            if (event instanceof ContextRefreshedEvent) {
                Map<String, AmqpContextAware> awares = context.getBeansOfType(AmqpContextAware.class);
                for (AmqpContextAware aware : awares.values()) {
                    aware.setAmqpContext(rabbitContext);
                }
            }

            if (context instanceof ConfigurableApplicationContext) {
                ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) context;
                injectAll(configurableApplicationContext, rabbitContext.getExchanges());
                injectAll(configurableApplicationContext, rabbitContext.getQueues());
                injectAll(configurableApplicationContext, rabbitContext.getBindings());
                injectAll(configurableApplicationContext, rabbitContext.getContainers());

                inject(configurableApplicationContext, rabbitContext.getRabbitTemplate());
                inject(configurableApplicationContext, rabbitContext.getAdmin());
                inject(configurableApplicationContext, rabbitContext.getMessageConverter());
                inject(configurableApplicationContext, rabbitContext.getMessageConverter().getClassMapper());
            }

            rabbitContext.start();
        }
    }

    private void injectAll(ConfigurableApplicationContext context, Collection beans) {
        if (beans != null) {
            beans.forEach(bean -> inject(context, bean));
        }
    }

    private void inject(ConfigurableApplicationContext context, Object bean) {
        if (bean != null) {
            context.getBeanFactory().registerSingleton(singletonBeanName(bean), bean);

            if (bean instanceof InitializingBean) {
                InitializingBean initializingBean = (InitializingBean) bean;
                try {
                    initializingBean.afterPropertiesSet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (bean instanceof ApplicationContextAware) {
                ApplicationContextAware contextAware = (ApplicationContextAware) bean;
                try {
                    contextAware.setApplicationContext(context);
                } catch (BeansException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String singletonBeanName(Object bean) {
        return bean.getClass().getSimpleName() + "@" + UUID.randomUUID().toString();
    }
}
