package com.familyspencesapi.messages.task;

import com.familyspencesapi.config.messages.task.TaskProducerQueueConfig;
import com.familyspencesapi.utils.MessageSender;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class TaskMessagePublisher implements MessageSender<Object> {

    private final RabbitTemplate rabbitTemplate;
    private final TaskProducerQueueConfig config;
    private final Gson gson = new GsonBuilder().create();

    public TaskMessagePublisher(RabbitTemplate rabbitTemplate, TaskProducerQueueConfig config) {
        this.rabbitTemplate = rabbitTemplate;
        this.config = config;

        // Debug: Verifica que las propiedades se carguen correctamente
        System.out.println("🔧 Config cargada:");
        System.out.println("Exchange: " + config.getExchangeName());
        System.out.println("Create Routing Key: " + config.getCreateRoutingKey());
    }

    @Override
    public void execute(Object message, String routingKey) {
        send(message, config.getExchangeName(), routingKey);
    }

    public void publishTaskCreated(Object task) {
        System.out.println("🚀 Intentando publicar task created...");
        send(task, config.getExchangeName(), config.getCreateRoutingKey());
    }

    public void publishTaskUpdated(Object task) {
        send(task, config.getExchangeName(), config.getUpdateRoutingKey());
    }

    public void publishTaskDeleted(Object task) {
        send(task, config.getExchangeName(), config.getDeleteRoutingKey());
    }

    private void send(Object message, String exchange, String routingKey) {
        String jsonMessage = gson.toJson(message);
        MessageProperties properties = new MessageProperties();
        properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        Message amqpMessage = new Message(jsonMessage.getBytes(), properties);

        System.out.println("📤 Enviando mensaje:");
        System.out.println("   Exchange: " + exchange);
        System.out.println("   Routing Key: " + routingKey);
        System.out.println("   Mensaje: " + jsonMessage);

        rabbitTemplate.send(exchange, routingKey, amqpMessage);

        System.out.println("✅ Mensaje enviado exitosamente");
    }
}