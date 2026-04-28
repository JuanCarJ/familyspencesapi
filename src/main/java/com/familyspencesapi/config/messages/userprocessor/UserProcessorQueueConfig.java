package com.familyspencesapi.config.messages.userprocessor;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserProcessorQueueConfig {

    public static final String USER_EXCHANGE = "x.user.exchange";
    public static final String USER_UPDATE_QUEUE = "q.user.update";
    public static final String USER_DEACTIVATE_QUEUE = "q.user.deactivate";

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    @Bean
    public Queue updateUserQueue() {
        return new Queue(USER_UPDATE_QUEUE, true);
    }

    @Bean
    public Queue deactivateUserQueue() {
        return new Queue(USER_DEACTIVATE_QUEUE, true);
    }

    @Bean
    public Binding updateUserBinding() {
        return BindingBuilder.bind(updateUserQueue()).to(userExchange()).with("user.update");
    }

    @Bean
    public Binding deactivateUserBinding() {
        return BindingBuilder.bind(deactivateUserQueue()).to(userExchange()).with("user.deactivate");
    }
}
