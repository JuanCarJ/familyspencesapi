package com.familyspencesapi.config.messages.task;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "task.producer")
@PropertySource("classpath:task.properties")
public class TaskProducerQueueConfig {

    private String exchangeName;
    private String createRoutingKey;
    private String updateRoutingKey;
    private String deleteRoutingKey;

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getCreateRoutingKey() {
        return createRoutingKey;
    }

    public void setCreateRoutingKey(String createRoutingKey) {
        this.createRoutingKey = createRoutingKey;
    }

    public String getUpdateRoutingKey() {
        return updateRoutingKey;
    }

    public void setUpdateRoutingKey(String updateRoutingKey) {
        this.updateRoutingKey = updateRoutingKey;
    }

    public String getDeleteRoutingKey() {
        return deleteRoutingKey;
    }

    public void setDeleteRoutingKey(String deleteRoutingKey) {
        this.deleteRoutingKey = deleteRoutingKey;
    }
}
