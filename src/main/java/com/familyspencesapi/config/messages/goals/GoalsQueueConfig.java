package com.familyspencesapi.config.messages.goals;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoalsQueueConfig {

    @Value("${goal.exchange.name:x.goal.exchange}")
    private String exchangeName;

    @Value("${goal.routing.key.create:goal.create}")
    private String routingKeyCreate;

    @Value("${goal.routing.key.update:goal.update}")
    private String routingKeyUpdate;

    @Value("${goal.routing.key.delete:goal.delete}")
    private String routingKeyDelete;

    @Bean
    public DirectExchange goalExchange() {
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