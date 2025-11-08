package com.familyspencesapi.messages.vacation;

import com.familyspencesapi.config.messages.vacation.VacationQueueConfig;
import com.familyspencesapi.domain.vacation.Vacation;
import com.familyspencesapi.utils.MessageSender;
import com.familyspencesapi.utils.gson.MapperJsonObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class VacationMessageSender implements MessageSender<Object> {

    private final RabbitTemplate rabbitTemplate;
    private final VacationQueueConfig vacationQueueConfig;
    private final MapperJsonObject mapperJsonObject;

    public VacationMessageSender(RabbitTemplate rabbitTemplate,
                                 VacationQueueConfig vacationQueueConfig,
                                 MapperJsonObject mapperJsonObject) {
        this.rabbitTemplate = rabbitTemplate;
        this.vacationQueueConfig = vacationQueueConfig;
        this.mapperJsonObject = mapperJsonObject;
    }

    @Override
    public void execute(Object message, String routingKey) {
        mapperJsonObject.execute(message).ifPresent(json -> {
            MessageProperties props = new MessageProperties();
            props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            Message amqpMessage = new Message(json.getBytes(), props);
            rabbitTemplate.send(
                    vacationQueueConfig.getExchangeName(),
                    routingKey,
                    amqpMessage
            );
        });
    }

    public void sendVacationCreated(Vacation vacation) {
        execute(vacation, vacationQueueConfig.getRoutingKeyCreate());
    }

    public void sendVacationUpdated(Vacation vacation) {
        execute(vacation, vacationQueueConfig.getRoutingKeyUpdate());
    }

    public void sendVacationDeleted(Map<String, String> data) {
        execute(data, vacationQueueConfig.getRoutingKeyDelete());
    }
}