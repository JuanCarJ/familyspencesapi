package com.familyspencesapi.config.messages.budgetprocessor.expense;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "budget.procesar")
@PropertySource("classpath:budget.properties")
public class BudgetExpenseProcessQueueConfig {

    private String exchangeName;

    private String routingKeyExpenseCreate;
    private String routingKeyExpenseUpdate;
    private String routingKeyExpenseDelete;

    private String queueExpenseCreate;
    private String queueExpenseUpdate;
    private String queueExpenseDelete;

    public String getExchangeName()
    {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getRoutingKeyExpenseCreate() {
        return routingKeyExpenseCreate;
    }

    public void setRoutingKeyExpenseCreate(String routingKeyExpenseCreate) {
        this.routingKeyExpenseCreate = routingKeyExpenseCreate;
    }

    public String getRoutingKeyExpenseUpdate() {
        return routingKeyExpenseUpdate;
    }

    public void setRoutingKeyExpenseUpdate(String routingKeyExpenseUpdate) {
        this.routingKeyExpenseUpdate = routingKeyExpenseUpdate;
    }

    public String getRoutingKeyExpenseDelete() {
        return routingKeyExpenseDelete;
    }

    public void setRoutingKeyExpenseDelete(String routingKeyExpenseDelete) {
        this.routingKeyExpenseDelete = routingKeyExpenseDelete;
    }

    public String getQueueExpenseCreate() {
        return queueExpenseCreate;
    }

    public void setQueueExpenseCreate(String queueExpenseCreate) {
        this.queueExpenseCreate = queueExpenseCreate;
    }

    public String getQueueExpenseUpdate() {
        return queueExpenseUpdate;
    }

    public void setQueueExpenseUpdate(String queueExpenseUpdate) {
        this.queueExpenseUpdate = queueExpenseUpdate;
    }

    public String getQueueExpenseDelete() {
        return queueExpenseDelete;
    }

    public void setQueueExpenseDelete(String queueExpenseDelete) {
        this.queueExpenseDelete = queueExpenseDelete;
    }
}
