package com.familyspencesapi.messages.users;

import com.familyspencesapi.config.messages.userprocessor.registeruser.UserRegisterProcessQueueConfig;
import com.familyspencesapi.domain.users.RegisterUserMessage;
import com.familyspencesapi.utils.MessageSender;
import com.familyspencesapi.utils.gson.MapperJsonObjectUser;
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
public class MessageSenderBrokerUser implements MessageSender<RegisterUserMessage> {

    private static final Logger log = LoggerFactory.getLogger(MessageSenderBrokerUser.class);

    private final RabbitTemplate rabbitTemplate;
    private final MapperJsonObjectUser mapperJson;
    private final UserRegisterProcessQueueConfig queueConfig;

    public MessageSenderBrokerUser(RabbitTemplate rabbitTemplate,
                                   MapperJsonObjectUser mapperJson,
                                   UserRegisterProcessQueueConfig queueConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.mapperJson = mapperJson;
        this.queueConfig = queueConfig;
    }

    @Override
    public void execute(RegisterUserMessage message, String routingKey) {
        // Convertimos el mensaje a JSON
        Optional<String> messageText = mapperJson.execute(message);

        if (messageText.isPresent()) {
            // Enviamos el JSON directamente al exchange con la routing key
            rabbitTemplate.convertAndSend(queueConfig.getExchangeName(), routingKey, message);

            log.info("Mensaje enviado correctamente a RabbitMQ -> Exchange: {}, RoutingKey: {},Nombre: {}, Email: {}, Documento: {}",
                    queueConfig.getExchangeName(), routingKey, message.firstName(), message.email(), message.document());
        } else {
            log.warn("⚠️ No se pudo serializar el mensaje RegisterUser: {}", message);
        }
    }

    private MessageProperties getMessageProperties(String idMessageSender) {
        return MessagePropertiesBuilder.newInstance()
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setHeader("idMessage", idMessageSender)
                .build();
    }

    private Optional<Message> getMessageBody(Object message, MessageProperties messageProperties) {
        Optional<String> messageText = mapperJson.execute(message);
        return messageText.map(msg -> MessageBuilder
                .withBody(msg.getBytes())
                .andProperties(messageProperties)
                .build());
    }
}
