package com.familyspencesapi.config.messages.task;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTaskConfig {

    public static final String TASK_EXCHANGE = "x.task.events";

    @Bean
    public TopicExchange taskExchange() {
        return new TopicExchange(TASK_EXCHANGE, true, false);
    }
}