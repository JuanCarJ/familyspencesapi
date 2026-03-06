package com.familyspencesapi.config.messages.userprocessor.user;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "user.procesar")
@PropertySource("classpath:users.properties")
public class FamilyMemberUserProcessQueueConfig {

    private String exchangeName;
    private String routingKeyAddMember;
    private String queueAddMember;

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getRoutingKeyAddMember() {
        return routingKeyAddMember;
    }

    public void setRoutingKeyAddMember(String routingKeyAddMember) {
        this.routingKeyAddMember = routingKeyAddMember;
    }

    public String getQueueAddMember() {
        return queueAddMember;
    }

    public void setQueueAddMember(String queueAddMember) {
        this.queueAddMember = queueAddMember;
    }
}
