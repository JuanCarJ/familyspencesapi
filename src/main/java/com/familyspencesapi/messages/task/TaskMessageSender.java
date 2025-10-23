package com.familyspencesapi.messages.task;

import com.familyspencesapi.config.messages.tasks.TaskProducerQueueConfig;
import com.familyspencesapi.utils.MessageSender;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class TaskMessageSender implements MessageSender<Object> {

    private final RabbitTemplate rabbitTemplate;
    private final TaskProducerQueueConfig config;
    private final Gson gson = new GsonBuilder().create();

    public TaskMessageSender(RabbitTemplate rabbitTemplate, TaskProducerQueueConfig config) {
        this.rabbitTemplate = rabbitTemplate;
        this.config = config;
    }

    @Override
    public void execute(Object message, String routingKey) { }

    public void sendCreate(Object message) {
        send(message, config.getExchangeName(), config.getRoutingKeyCreate());
    }

    public void sendUpdate(Object message) {
        send(message, config.getExchangeName(), config.getRoutingKeyUpdate());
    }

    public void sendDelete(Object message) {
        send(message, config.getExchangeName(), config.getRoutingKeyDelete());
    }

    private void send(Object message, String exchange, String routingKey) {
        String jsonMessage = gson.toJson(message);
        MessageProperties props = new MessageProperties();
        props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        Message amqpMessage = new Message(jsonMessage.getBytes(), props);
        rabbitTemplate.send(exchange, routingKey, amqpMessage);
        System.out.println("📤 Mensaje enviado a RabbitMQ → " + exchange + " | " + routingKey);
    }
}
