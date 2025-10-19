package com.familyspencesapi.messages.balance;

import com.familyspencesapi.domain.home.MonthlyClosing;
import com.familyspencesapi.utils.MessageSender;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class BalanceMessageSenderBroker implements MessageSender<MonthlyClosing> {

    private final RabbitTemplate rabbitTemplate;
    private final Gson gson;

    public BalanceMessageSenderBroker(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new com.google.gson.JsonSerializer<LocalDate>() {
                    @Override
                    public com.google.gson.JsonElement serialize(LocalDate date, java.lang.reflect.Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
                        return new com.google.gson.JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
                    }
                }).create();
    }

    @Override
    public void execute(MonthlyClosing message, String routingKey) {
    }

    public void send(MonthlyClosing message, String exchange, String routingKey) {
        String jsonMessage = gson.toJson(message);
        MessageProperties properties = new MessageProperties();
        properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        Message amqpMessage = new Message(jsonMessage.getBytes(), properties);
        rabbitTemplate.send(exchange, routingKey, amqpMessage);
    }
}