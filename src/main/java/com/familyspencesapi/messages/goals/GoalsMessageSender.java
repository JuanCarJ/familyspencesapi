package com.familyspencesapi.messages.goals;

import com.familyspencesapi.config.messages.goals.GoalsQueueConfig;
import com.familyspencesapi.domain.goals.Goal;
import com.familyspencesapi.utils.MessageSender;
import com.familyspencesapi.utils.gson.MapperJsonObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GoalsMessageSender implements MessageSender<Object> {

    private final RabbitTemplate rabbitTemplate;
    private final GoalsQueueConfig goalsQueueConfig;
    private final MapperJsonObject mapperJsonObject;

    public GoalsMessageSender(RabbitTemplate rabbitTemplate,
                              GoalsQueueConfig goalsQueueConfig,
                              MapperJsonObject mapperJsonObject) {
        this.rabbitTemplate = rabbitTemplate;
        this.goalsQueueConfig = goalsQueueConfig;
        this.mapperJsonObject = mapperJsonObject;
    }

    @Override
    public void execute(Object message, String routingKey) {
        mapperJsonObject.execute(message).ifPresent(json -> {
            MessageProperties props = new MessageProperties();
            props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            Message amqpMessage = new Message(json.getBytes(), props);
            rabbitTemplate.send(goalsQueueConfig.getExchangeName(), routingKey, amqpMessage);
        });
    }

    public void sendGoalCreated(Goal goal) {
        execute(goal, goalsQueueConfig.getRoutingKeyCreate());
    }

    public void sendGoalUpdated(Goal goal) {
        execute(goal, goalsQueueConfig.getRoutingKeyUpdate());
    }

    public void sendGoalDeleted(Map<String, String> data) {
        execute(data, goalsQueueConfig.getRoutingKeyDelete());
    }
}