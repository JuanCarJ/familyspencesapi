package com.familyspencesapi.messages.pets;

import com.familyspencesapi.config.messages.pets.PetsQueueConfig;
import com.familyspencesapi.config.messages.pets.dto.petsDTO;
import com.familyspencesapi.domain.pet.Pet;
import com.familyspencesapi.utils.MessageSender;
import com.familyspencesapi.utils.gson.MapperJsonObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PetsMessageSender implements MessageSender<Object> {

    private final RabbitTemplate rabbitTemplate;
    private final PetsQueueConfig petsQueueConfig;
    private final MapperJsonObject mapperJsonObject;

    public PetsMessageSender(RabbitTemplate rabbitTemplate,
                             PetsQueueConfig petsQueueConfig,
                             MapperJsonObject mapperJsonObject) {
        this.rabbitTemplate = rabbitTemplate;
        this.petsQueueConfig = petsQueueConfig;
        this.mapperJsonObject = mapperJsonObject;
    }

    @Override
    public void execute(Object message, String routingKey) {
        mapperJsonObject.execute(message).ifPresent(json -> {
            MessageProperties props = new MessageProperties();
            props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            Message amqpMessage = new Message(json.getBytes(), props);
            rabbitTemplate.send(petsQueueConfig.getExchangeName(), routingKey, amqpMessage);
        });
    }

    // ==========================
    // MÉTODOS DE ENVÍO
    // ==========================

    public void sendPetCreated(Pet pet) {
        petsDTO petDTO = new petsDTO(pet);
        execute(petDTO, petsQueueConfig.getRoutingKeyCreate());
    }

    public void sendPetUpdated(Pet pet) {
        petsDTO petDTO = new petsDTO(pet);
        execute(petDTO, petsQueueConfig.getRoutingKeyUpdate());
    }

    public void sendPetDeleted(Map<String, String> data) {
        execute(data, petsQueueConfig.getRoutingKeyDelete());
    }
}