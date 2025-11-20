package com.familyspencesapi.config.messages.vacation;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VacationQueueConfig {

    @Value("${vacation.exchange.name:vacation-exchange}")
    private String exchangeName;

    @Value("${vacation.queue.name:vacation-events}")
    private String queueName;

    @Value("${vacation.routing.key.create:vacation.created}")
    private String routingKeyCreate;

    @Value("${vacation.routing.key.update:vacation.updated}")
    private String routingKeyUpdate;

    @Value("${vacation.routing.key.delete:vacation.deleted}")
    private String routingKeyDelete;

    @Bean
    public TopicExchange vacationExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Queue vacationQueue() {
        return new Queue(queueName, true); // durable = true
    }

    @Bean
    public Binding vacationBinding(Queue vacationQueue, TopicExchange vacationExchange) {
        return BindingBuilder
                .bind(vacationQueue)
                .to(vacationExchange)
                .with("vacation.*"); // Escucha todos los eventos vacation.*
    }

    // Getters
    public String getExchangeName() {
        return exchangeName;
    }

    public String getQueueName() {
        return queueName;
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