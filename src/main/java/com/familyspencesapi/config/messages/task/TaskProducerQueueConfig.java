package com.familyspencesapi.config.messages.tasks;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "task.producer")
@PropertySource("classpath:task.properties")
public class TaskProducerQueueConfig {

    private String exchangeName;
    private String routingKeyCreate;
    private String routingKeyUpdate;
    private String routingKeyDelete;

    public String getExchangeName() { return exchangeName; }
    public void setExchangeName(String exchangeName) { this.exchangeName = exchangeName; }

    public String getRoutingKeyCreate() { return routingKeyCreate; }
    public void setRoutingKeyCreate(String routingKeyCreate) { this.routingKeyCreate = routingKeyCreate; }

    public String getRoutingKeyUpdate() { return routingKeyUpdate; }
    public void setRoutingKeyUpdate(String routingKeyUpdate) { this.routingKeyUpdate = routingKeyUpdate; }

    public String getRoutingKeyDelete() { return routingKeyDelete; }
    public void setRoutingKeyDelete(String routingKeyDelete) { this.routingKeyDelete = routingKeyDelete; }
}
