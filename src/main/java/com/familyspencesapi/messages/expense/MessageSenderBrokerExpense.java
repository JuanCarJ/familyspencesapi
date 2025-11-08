package com.familyspencesapi.messages.expense;

import com.familyspencesapi.config.messages.budgetprocessor.expense.BudgetExpenseProcessQueueConfig;
import com.familyspencesapi.domain.expense.Expense;
import com.familyspencesapi.utils.MessageSender;
import com.familyspencesapi.utils.gson.MapperJsonObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Component
public class MessageSenderBrokerExpense implements MessageSender<Expense> {

    private final RabbitTemplate rabbitTemplate;
    private final MapperJsonObject mapperJson;
    private final BudgetExpenseProcessQueueConfig queueConfig;
    private static final Logger log = LoggerFactory.getLogger(MessageSenderBrokerExpense.class);

    public MessageSenderBrokerExpense(RabbitTemplate rabbitTemplate, MapperJsonObject mapper, BudgetExpenseProcessQueueConfig queueConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.mapperJson = mapper;
        this.queueConfig = queueConfig;
    }

    @Override
    public void execute(Expense message, String routingKey) {
        MessageProperties messageProperties = getMessageProperties(routingKey);

        Optional<Message> messageBody = getMessageBody(message, messageProperties);
        if (messageBody.isPresent()) {

            rabbitTemplate.convertAndSend(queueConfig.getExchangeName(),routingKey, messageBody.get());
        }else {
        log.warn("No se pudo serializar el mensaje Expense: {}", message);}
    }

    public MessageProperties getMessageProperties(String idMessageSender) {
        return MessagePropertiesBuilder.newInstance()
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setHeader("idMessage",idMessageSender)
                .build();
    }

    public Optional<Message> getMessageBody(Object message, MessageProperties messageProperties) {

        Optional<String> messageText = mapperJson.execute(message);
        return messageText.map(msg -> MessageBuilder
                .withBody(msg.getBytes())
                .andProperties(messageProperties)
                .build());
    }

}
