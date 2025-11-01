package com.familyspencesapi.messages.ranking;

import com.familyspencesapi.config.messages.budgetprocessor.ranking.RankingProcessQueueConfig;
import com.familyspencesapi.domain.ranking.Ranking;
import com.familyspencesapi.utils.MessageSender;
import com.familyspencesapi.utils.gson.MapperJsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MessageSenderBrokerRanking implements MessageSender<Ranking> {

    private final RabbitTemplate rabbitTemplate;
    private final MapperJsonObject mapperJson;
    private final RankingProcessQueueConfig queueConfig;
    private static final Logger log = LoggerFactory.getLogger(MessageSenderBrokerRanking.class);

    public MessageSenderBrokerRanking(RabbitTemplate rabbitTemplate, MapperJsonObject mapperJson, RankingProcessQueueConfig queueConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.mapperJson = mapperJson;
        this.queueConfig = queueConfig;
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

    @Override
    public void execute(Ranking message, String routingKey) {
        MessageProperties messageProperties = getMessageProperties(routingKey);

        Optional<Message> messageBody = getMessageBody(message, messageProperties);
        if (messageBody.isPresent()) {

            rabbitTemplate.convertAndSend(queueConfig.getExchangeName(),routingKey, messageBody.get());

        }else {
            log.warn("No se pudo serializar el mensaje Ranking: {}", message);}

    }
}
