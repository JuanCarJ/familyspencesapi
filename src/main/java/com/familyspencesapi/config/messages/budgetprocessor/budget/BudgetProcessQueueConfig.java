package com.familyspencesapi.config.messages.budgetprocessor.budget;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "budget.procesar.budget")
@PropertySource("classpath:budget.properties")
public class BudgetProcessQueueConfig {

    public static final String BUDGET_EXCHANGE_NAME = "x.budget.exchange";
    public static final String BUDGET_QUEUE_CREATE = "q.budget.create";
    public static final String BUDGET_QUEUE_UPDATE = "q.budget.update";
    public static final String BUDGET_QUEUE_DELETE = "q.budget.delete";

    private String exchangeName;
    private String routingKeyCreate;
    private String routingKeyUpdate;
    private String routingKeyDelete;

    @Bean
    public TopicExchange budgetExchange() {
        return new TopicExchange(BUDGET_EXCHANGE_NAME);
    }

    @Bean
    public Queue budgetCreateQueue() {
        return new Queue(BUDGET_QUEUE_CREATE, true);
    }

    @Bean
    public Queue budgetUpdateQueue() {
        return new Queue(BUDGET_QUEUE_UPDATE, true);
    }

    @Bean
    public Queue budgetDeleteQueue() {
        return new Queue(BUDGET_QUEUE_DELETE, true);
    }

    @Bean
    public Binding budgetCreateBinding() {
        return BindingBuilder
                .bind(budgetCreateQueue())
                .to(budgetExchange())
                .with(getRoutingKeyCreate());
    }

    @Bean
    public Binding budgetUpdateBinding() {
        return BindingBuilder
                .bind(budgetUpdateQueue())
                .to(budgetExchange())
                .with(getRoutingKeyUpdate());
    }

    @Bean
    public Binding budgetDeleteBinding() {
        return BindingBuilder
                .bind(budgetDeleteQueue())
                .to(budgetExchange())
                .with(getRoutingKeyDelete());
    }

    // Getters y Setters
    public String getExchangeName() {
        return BUDGET_EXCHANGE_NAME;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getRoutingKeyCreate() {
        return routingKeyCreate;
    }

    public void setRoutingKeyCreate(String routingKeyCreate) {
        this.routingKeyCreate = routingKeyCreate;
    }

    public String getRoutingKeyUpdate() {
        return routingKeyUpdate;
    }

    public void setRoutingKeyUpdate(String routingKeyUpdate) {
        this.routingKeyUpdate = routingKeyUpdate;
    }

    public String getRoutingKeyDelete() {
        return routingKeyDelete;
    }

    public void setRoutingKeyDelete(String routingKeyDelete) {
        this.routingKeyDelete = routingKeyDelete;
    }
}