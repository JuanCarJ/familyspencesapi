package com.familyspencesapi.config.messages.budgetprocessor.ranking;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix ="budget.procesar.ranking" )
@PropertySource("classpath:budget.properties")
public class RankingProcessQueueConfig {

    private String exchangeName;
    private String routingKeyCreate;


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
}
