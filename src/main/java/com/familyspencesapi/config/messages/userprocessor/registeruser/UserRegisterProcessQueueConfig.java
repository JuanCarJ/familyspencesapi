package com.familyspencesapi.config.messages.userprocessor.registeruser;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "user.register")
@PropertySource("classpath:users.properties")
public class UserRegisterProcessQueueConfig {

    private String exchangeName;

    private String routingKeyUserCreate;
    private String routingKeyUserUpdate;
    private String routingKeyUserDelete;

    private String queueUserCreate;
    private String queueUserUpdate;
    private String queueUserDelete;

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getRoutingKeyUserCreate() {
        return routingKeyUserCreate;
    }

    public void setRoutingKeyUserCreate(String routingKeyUserCreate) {
        this.routingKeyUserCreate = routingKeyUserCreate;
    }

    public String getRoutingKeyUserUpdate() {
        return routingKeyUserUpdate;
    }

    public void setRoutingKeyUserUpdate(String routingKeyUserUpdate) {
        this.routingKeyUserUpdate = routingKeyUserUpdate;
    }

    public String getRoutingKeyUserDelete() {
        return routingKeyUserDelete;
    }

    public void setRoutingKeyUserDelete(String routingKeyUserDelete) {
        this.routingKeyUserDelete = routingKeyUserDelete;
    }

    public String getQueueUserCreate() {
        return queueUserCreate;
    }

    public void setQueueUserCreate(String queueUserCreate) {
        this.queueUserCreate = queueUserCreate;
    }

    public String getQueueUserUpdate() {
        return queueUserUpdate;
    }

    public void setQueueUserUpdate(String queueUserUpdate) {
        this.queueUserUpdate = queueUserUpdate;
    }

    public String getQueueUserDelete() {
        return queueUserDelete;
    }

    public void setQueueUserDelete(String queueUserDelete) {
        this.queueUserDelete = queueUserDelete;
    }

}
