package com.familyspencesapi.messages.income;
import com.familyspencesapi.config.messages.income.IncomeQueueConfig;
import com.familyspencesapi.domain.income.Income;
import com.familyspencesapi.utils.MessageSender;
import com.familyspencesapi.utils.gson.MapperJsonObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class MessageSenderBrokerIncome  implements MessageSender<Object>{

    private final RabbitTemplate rabbitTemplate;
    private final IncomeQueueConfig incomeQueueConfig;
    private final MapperJsonObject mapperJsonObject;

    public MessageSenderBrokerIncome(RabbitTemplate rabbitTemplate, IncomeQueueConfig incomeQueueConfig, MapperJsonObject mapperJsonObject) {
        this.rabbitTemplate = rabbitTemplate;
        this.incomeQueueConfig = incomeQueueConfig;
        this.mapperJsonObject = mapperJsonObject;
    }

    @Override
    public void execute(Object message, String routingKey) {
        mapperJsonObject.execute(message).ifPresent(json -> {
            MessageProperties props = new MessageProperties();
            props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            Message amqpMessage = new Message(json.getBytes(), props);
            rabbitTemplate.send(
                    incomeQueueConfig.getExchangeName(),
                    routingKey,
                    amqpMessage
            );
        });
    }

    public void sendIncomeCreated(Income income) {
        execute(income, incomeQueueConfig.getRoutingKeyCreate());
    }

    public void sendIncomeUpdated(Income income) {
        execute(income, incomeQueueConfig.getRoutingKeyUpdate());
    }

    public void sendIncomeDeleted(Map<String, String> data) {
        execute(data, incomeQueueConfig.getRoutingKeyDelete());
    }
}