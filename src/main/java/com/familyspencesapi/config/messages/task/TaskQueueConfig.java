package com.familyspencesapi.config.messages.task;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskQueueConfig {

    @Value("${task.exchange.name:x.task.exchange}")
    private String exchangeName;

    @Value("${task.routing.key.create:task.create}")
    private String routingKeyCreate;

    @Value("${task.routing.key.update:task.update}")
    private String routingKeyUpdate;

    @Value("${task.routing.key.delete:task.delete}")
    private String routingKeyDelete;

    @Bean
    public DirectExchange taskExchange() {
        return new DirectExchange(exchangeName);
    }


    public String getExchangeName() { return exchangeName; }
    public String getRoutingKeyCreate() { return routingKeyCreate; }
    public String getRoutingKeyUpdate() { return routingKeyUpdate; }
    public String getRoutingKeyDelete() { return routingKeyDelete; }
}
