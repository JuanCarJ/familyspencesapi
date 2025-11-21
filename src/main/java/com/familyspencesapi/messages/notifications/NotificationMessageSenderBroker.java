package com.familyspencesapi.messages.notifications;

import com.familyspencesapi.utils.MessageSender;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationMessageSenderBroker implements MessageSender<Map<String, Object>> {

    private final RabbitTemplate rabbitTemplate;
    private final Gson gson;

    // Deben coincidir con NotificationQueueConfig del procesador
    public static final String NOTIFICATION_EXCHANGE = "familyspences.notifications.exchange";
    public static final String NOTIFICATION_ROUTING_KEY = "familyspences.notifications.key";

    public NotificationMessageSenderBroker(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.gson = new GsonBuilder().create();
    }

    @Override
    public void execute(Map<String, Object> message, String routingKey) {
        String keyToUse = (routingKey == null || routingKey.isBlank())
                ? NOTIFICATION_ROUTING_KEY
                : routingKey;

        send(message, NOTIFICATION_EXCHANGE, keyToUse);
    }

    public void send(Map<String, Object> message, String exchange, String routingKey) {
        String jsonMessage = gson.toJson(message);

        MessageProperties properties = new MessageProperties();
        properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);

        Message amqpMessage = new Message(jsonMessage.getBytes(), properties);
        rabbitTemplate.send(exchange, routingKey, amqpMessage);
    }
}
