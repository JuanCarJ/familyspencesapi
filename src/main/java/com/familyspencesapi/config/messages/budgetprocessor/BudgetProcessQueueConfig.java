package com.familyspencesapi.config.messages.budgetprocessor;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "budget.procesar")
@PropertySource("classpath:budget.properties")
public class BudgetProcessQueueConfig {

    private String exchangeName;

    private String routingKeyCreate;
    private String routingKeyUpdate;
    private String routingKeyDelete;

    private String queueCreate;
    private String queueUpdate;
    private String queueDelete;

    public String getExchangeName() {
        return exchangeName;
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

    public String getQueueCreate() {
        return queueCreate;
    }

    public void setQueueCreate(String queueCreate) {
        this.queueCreate = queueCreate;
    }

    public String getQueueUpdate() {
        return queueUpdate;
    }

    public void setQueueUpdate(String queueUpdate) {
        this.queueUpdate = queueUpdate;
    }

    public String getQueueDelete() {
        return queueDelete;
    }

    public void setQueueDelete(String queueDelete) {
        this.queueDelete = queueDelete;
    }
}
