package com.familyspencesapi.config.messages.income;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:income.properties")
public class IncomeQueueConfig {

    @Value("${income.exchange.name:x.income.exchange}")
    private String exchangeName;

    @Value("${income.routing.key.create:income.create}")
    private String routingKeyCreate;

    @Value("${income.routing.key.delete:income.delete}")
    private String routingKeyDelete;

    @Value("${income.routing.key.update:income.update}")
    private String routingKeyUpdate;

    @Bean
    public DirectExchange incomeExchange() {
        return new DirectExchange(exchangeName);
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getRoutingKeyCreate() {
        return routingKeyCreate;
    }

    public String getRoutingKeyDelete() {
        return routingKeyDelete;
    }

    public String getRoutingKeyUpdate() {
        return routingKeyUpdate;
    }
}