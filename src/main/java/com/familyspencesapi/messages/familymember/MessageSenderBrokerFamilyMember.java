package com.familyspencesapi.messages.familymember;

import com.familyspencesapi.config.messages.userprocessor.user.FamilyMemberUserProcessQueueConfig;
import com.familyspencesapi.domain.users.RegisterUser;
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
public class MessageSenderBrokerFamilyMember implements MessageSender<RegisterUser> {

    private final RabbitTemplate rabbitTemplate;
    private final MapperJsonObject mapperJson;
    private final FamilyMemberUserProcessQueueConfig queueConfig;
    private static final Logger log = LoggerFactory.getLogger(MessageSenderBrokerFamilyMember.class);

    public MessageSenderBrokerFamilyMember(RabbitTemplate rabbitTemplate, MapperJsonObject mapperJson, FamilyMemberUserProcessQueueConfig queueConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.mapperJson = mapperJson;
        this.queueConfig = queueConfig;
    }


    @Override
    public void execute(RegisterUser message, String routingKey) {
        MessageProperties messageProperties = getMessageProperties(routingKey);

        Optional<Message> messageBody = getMessageBody(message, messageProperties);
        if (messageBody.isPresent()) {

            rabbitTemplate.convertAndSend(queueConfig.getExchangeName(),routingKey, messageBody.get());
        }else {
            log.warn("No se pudo serializar el mensaje FamilyMember: {}", message);}
    }


    public org.springframework.amqp.core.MessageProperties getMessageProperties(String idMessageSender) {
        return MessagePropertiesBuilder.newInstance()
                .setContentType(org.springframework.amqp.core.MessageProperties.CONTENT_TYPE_JSON)
                .setHeader("idMessage",idMessageSender)
                .build();
    }

    public Optional<org.springframework.amqp.core.Message> getMessageBody(Object message, org.springframework.amqp.core.MessageProperties messageProperties) {

        Optional<String> messageText = mapperJson.execute(message);
        return messageText.map(msg -> MessageBuilder
                .withBody(msg.getBytes())
                .andProperties(messageProperties)
                .build());
    }
}
