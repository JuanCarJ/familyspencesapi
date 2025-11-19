package com.familyspencesapi.config.messages.pets;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PetsQueueConfig {

    @Value("${pet.exchange.name:x.pet.exchange}")
    private String exchangeName;

    @Value("${pet.routing.key.create:pet.create}")
    private String routingKeyCreate;

    @Value("${pet.routing.key.update:pet.update}")
    private String routingKeyUpdate;

    @Value("${pet.routing.key.delete:pet.delete}")
    private String routingKeyDelete;

    @Bean
    public TopicExchange petExchange() {
        return new TopicExchange(exchangeName);
    }

    public String getExchangeName() { return exchangeName; }
    public String getRoutingKeyCreate() { return routingKeyCreate; }
    public String getRoutingKeyUpdate() { return routingKeyUpdate; }
    public String getRoutingKeyDelete() { return routingKeyDelete; }
}