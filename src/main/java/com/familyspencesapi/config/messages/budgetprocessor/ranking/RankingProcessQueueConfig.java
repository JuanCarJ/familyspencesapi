package com.familyspencesapi.config.messages.budgetprocessor.ranking;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix ="budget.procesar.ranking" )
@PropertySource("classpath:budget.properties")
public class RankingProcessQueueConfig {

    public static  final String RANKING_EXCHANGE_NAME = "x.ranking.exchange";
    public static  final String RANKING_QUEUE_NAME = "q.ranking.calculate";

    private String exchangeName;
    private String routingKeyCreate;

    @Bean
    public TopicExchange rankingExchange() {
        return new TopicExchange(RANKING_EXCHANGE_NAME);
    }

    @Bean
    public Queue rankingQueue() {
        return new Queue(RANKING_QUEUE_NAME, true);
    }

    @Bean
    public Binding rankingBinding() {
        return BindingBuilder
                .bind(rankingQueue())
                .to(rankingExchange())
                .with(getRoutingKeyCreate()); // Usa la routing key de tus properties
    }

    public String getExchangeName() {
        return RANKING_EXCHANGE_NAME;
    }

    public void setExchangeName(String exchangeName) {
    }

    public String getRoutingKeyCreate() {
        return routingKeyCreate;
    }

    public void setRoutingKeyCreate(String routingKeyCreate) {
        this.routingKeyCreate = routingKeyCreate;
    }
}
