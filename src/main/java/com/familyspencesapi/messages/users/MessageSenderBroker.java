package com.familyspencesapi.messages.users;

import com.familyspencesapi.utils.MessageSender;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageSenderBroker implements MessageSender<Object> {

    private final RabbitTemplate rabbitTemplate;
    private final Gson gson = new GsonBuilder().create();

    public MessageSenderBroker(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void execute(Object message, String routingKey) {
    }

    public void send(Object message, String exchange, String routingKey) {
        String jsonMessage = gson.toJson(message);
        MessageProperties properties = new MessageProperties();
        properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        Message amqpMessage = new Message(jsonMessage.getBytes(), properties);
        rabbitTemplate.send(exchange, routingKey, amqpMessage);
    }}