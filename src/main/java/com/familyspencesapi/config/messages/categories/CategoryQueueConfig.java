package com.familyspencesapi.config.messages.categories;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoryQueueConfig {

    @Value("${category.exchange.name:x.category.exchange}")
    private String exchangeName;

    @Value("${category.routing.key.create:category.create}")
    private String routingKeyCreate;

    @Value("${category.routing.key.update:category.update}")
    private String routingKeyUpdate;

    @Value("${category.routing.key.delete:category.delete}")
    private String routingKeyDelete;

    @Bean
    public DirectExchange categoryExchange() {
        return new DirectExchange(exchangeName);
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getRoutingKeyCreate() {
        return routingKeyCreate;
    }

    public String getRoutingKeyUpdate() {
        return routingKeyUpdate;
    }

    public String getRoutingKeyDelete() {
        return routingKeyDelete;
    }
}