package com.familyspencesapi.messages.budgetprocess.expense;

import com.familyspencesapi.domain.expense.Expense;
import com.familyspencesapi.utils.MessageSender;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageSenderBrokerExpense implements MessageSender<Expense> {

    private final RabbitTemplate rabbitTemplate;
    private final Gson gson = new GsonBuilder().create();

    public MessageSenderBrokerExpense(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void execute(Expense message, String routingKey) {
        MessageProperties messageProperties = getMessageProperties(routingKey);



    }

    public void send(Object message, String exchange, String routingKey) {
        String jsonMessage = gson.toJson(message);
        MessageProperties properties = new MessageProperties();
        properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        Message amqpMessage = new Message(jsonMessage.getBytes(), properties);
        rabbitTemplate.send(exchange, routingKey, amqpMessage);
    }

    public MessageProperties getMessageProperties(String idMessageSender) {
        return MessagePropertiesBuilder.newInstance()
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setHeader("idMessage",idMessageSender)
                .build();
    }

}
