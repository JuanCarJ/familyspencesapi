package com.familyspencesapi.messages.categories;

import com.familyspencesapi.config.messages.categories.CategoryQueueConfig;
import com.familyspencesapi.config.messages.categories.dto.CategoryDTO;
import com.familyspencesapi.domain.categories.Category;
import com.familyspencesapi.utils.MessageSender;
import com.familyspencesapi.utils.gson.MapperJsonObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CategoryMessageSender implements MessageSender<Object> {

    private final RabbitTemplate rabbitTemplate;
    private final CategoryQueueConfig categoryQueueConfig;
    private final MapperJsonObject mapperJsonObject;

    public CategoryMessageSender(RabbitTemplate rabbitTemplate,
                                 CategoryQueueConfig categoryQueueConfig,
                                 MapperJsonObject mapperJsonObject) {
        this.rabbitTemplate = rabbitTemplate;
        this.categoryQueueConfig = categoryQueueConfig;
        this.mapperJsonObject = mapperJsonObject;
    }

    @Override
    public void execute(Object message, String routingKey) {
        mapperJsonObject.execute(message).ifPresent(json -> {
            MessageProperties props = new MessageProperties();
            props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            Message amqpMessage = new Message(json.getBytes(), props);
            rabbitTemplate.send(categoryQueueConfig.getExchangeName(), routingKey, amqpMessage);
        });
    }

    public void sendCategoryCreated(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO(category);
        execute(categoryDTO, categoryQueueConfig.getRoutingKeyCreate());
    }

    public void sendCategoryUpdated(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO(category);
        execute(categoryDTO, categoryQueueConfig.getRoutingKeyUpdate());
    }

    public void sendCategoryDeleted(Map<String, String> data) {
        execute(data, categoryQueueConfig.getRoutingKeyDelete());
    }
}