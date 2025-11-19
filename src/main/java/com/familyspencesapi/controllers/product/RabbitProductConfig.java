package com.familyspencesapi.controllers.product;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitProductConfig {
    @Bean
    public DirectExchange productExchange() {
        return new DirectExchange("product.exchange");
    }
}
