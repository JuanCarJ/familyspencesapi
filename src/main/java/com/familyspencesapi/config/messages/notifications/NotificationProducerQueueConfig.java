package com.familyspencesapi.config.messages.notifications;

import org.springframework.stereotype.Component;

/**
 * Config del productor de notificaciones.
 * Debe coincidir con NotificationQueueConfig del procesador.
 */
@Component
public class NotificationProducerQueueConfig {

    public static final String EXCHANGE_NAME = "familyspences.notifications.exchange";
    public static final String ROUTING_KEY = "familyspences.notifications.key";

    public String getExchangeName() {
        return EXCHANGE_NAME;
    }

    public String getRoutingKey() {
        return ROUTING_KEY;
    }
}
