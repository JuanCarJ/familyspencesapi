package com.familyspencesapi.messages.task;

import com.familyspencesapi.config.messages.task.TaskQueueConfig;
import com.familyspencesapi.messages.task.dto.TaskDTO;
import com.familyspencesapi.domain.tasks.Tasks;
import com.familyspencesapi.utils.MessageSender;
import com.familyspencesapi.utils.gson.MapperJsonObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TaskMessageSender implements MessageSender<Object> {

    private final RabbitTemplate rabbitTemplate;
    private final TaskQueueConfig taskQueueConfig;
    private final MapperJsonObject mapperJsonObject;

    public TaskMessageSender(RabbitTemplate rabbitTemplate,
                                   TaskQueueConfig taskQueueConfig,
                                   MapperJsonObject mapperJsonObject) {
        this.rabbitTemplate = rabbitTemplate;
        this.taskQueueConfig = taskQueueConfig;
        this.mapperJsonObject = mapperJsonObject;
    }

    @Override
    public void execute(Object message, String routingKey) {
        mapperJsonObject.execute(message).ifPresent(json -> {
            MessageProperties props = new MessageProperties();
            props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            Message amqpMessage = new Message(json.getBytes(), props);
            rabbitTemplate.send(taskQueueConfig.getExchangeName(), routingKey, amqpMessage);
        });
    }

    public void sendTaskCreated(Tasks task) {
        TaskDTO taskDTO = new TaskDTO(task);
        execute(taskDTO, taskQueueConfig.getRoutingKeyCreate());
    }

    public void sendTaskUpdated(Tasks task) {
        TaskDTO taskDTO = new TaskDTO(task);
        execute(taskDTO, taskQueueConfig.getRoutingKeyUpdate());
    }

    public void sendTaskDeleted(Map<String, String> data) {
        execute(data, taskQueueConfig.getRoutingKeyDelete());
    }
}
