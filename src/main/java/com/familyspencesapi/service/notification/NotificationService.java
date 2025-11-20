package com.familyspencesapi.service.notification;

import com.familyspencesapi.config.messages.notifications.NotificationProducerQueueConfig;
import com.familyspencesapi.domain.notification.Notification;
import com.familyspencesapi.messages.notifications.NotificationMessageSenderBroker;
import com.familyspencesapi.repositories.notification.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final NotificationMessageSenderBroker messageSender;
    private final NotificationProducerQueueConfig config;

    public NotificationService(
            NotificationRepository notificationRepository,
            NotificationMessageSenderBroker messageSender,
            NotificationProducerQueueConfig config
    ) {
        this.notificationRepository = notificationRepository;
        this.messageSender = messageSender;
        this.config = config;
    }

    // ================== CONSULTAS ==================

    @Transactional(readOnly = true)
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUserId(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotificationsByUserId(UUID userId) {
        return notificationRepository.findUnreadByUserIdOrderedByPriorityAndDate(userId);
    }

    @Transactional(readOnly = true)
    public long getUnreadNotificationCount(UUID userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional(readOnly = true)
    public List<Notification> getRecentNotificationsByUserId(UUID userId) {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return notificationRepository.findRecentNotificationsByUserId(userId, since);
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUserIdAndType(
            UUID userId,
            Notification.NotificationType type
    ) {
        return notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type);
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUserIdAndPriority(
            UUID userId,
            Notification.NotificationPriority priority
    ) {
        return notificationRepository.findByUserIdAndPriorityOrderByCreatedAtDesc(userId, priority);
    }

    // ================== CREACIÓN ==================

    /**
     * Crea una notificación (dispara Rabbit y devuelve el objeto construido).
     * OJO: la persistencia real la hace el procesador al consumir el mensaje.
     */
    @Transactional
    public Notification createNotification(
            UUID userId,
            String message,
            Notification.NotificationType type,
            Notification.NotificationPriority priority
    ) {
        if (type == null) {
            type = Notification.NotificationType.INFO;
        }
        if (priority == null) {
            priority = Notification.NotificationPriority.NORMAL;
        }

        logger.info("Creating notification (async) for userId={}", userId);

        // Disparamos mensaje a Rabbit
        sendNotificationToBroker(userId, message, type, priority);

        // Devolvemos el objeto "local" (todavía no garantizamos que exista en BD)
        return new Notification(userId, message, type, priority);
    }

    // ================== CONSULTA POR ID ==================

    @Transactional(readOnly = true)
    public Optional<Notification> getNotificationById(UUID notificationId) {
        return notificationRepository.findById(notificationId);
    }

    // ================== MARCAR COMO LEÍDA ==================

    @Transactional
    public boolean markNotificationAsRead(UUID notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isEmpty()) {
            return false;
        }

        Notification notification = notificationOpt.get();
        notification.markAsRead();
        notificationRepository.save(notification);
        return true;
    }

    @Transactional
    public int markAllNotificationsAsReadByUserId(UUID userId) {
        LocalDateTime now = LocalDateTime.now();
        return notificationRepository.markAllAsReadByUserId(userId, now);
    }

    // ================== ELIMINACIÓN ==================

    @Transactional
    public boolean deleteNotification(UUID notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            return false;
        }
        notificationRepository.deleteById(notificationId);
        return true;
    }

    @Transactional
    public int deleteReadNotificationsByUserId(UUID userId) {
        return notificationRepository.deleteByUserIdAndReadTrue(userId);
    }

    // ================== ENVÍO EXPLÍCITO A RABBIT ==================

    /**
     * Método para otros servicios que quieran disparar notificaciones directamente.
     */
    @Transactional
    public void sendNotification(
            UUID userId,
            String message,
            Notification.NotificationType type,
            Notification.NotificationPriority priority
    ) {
        sendNotificationToBroker(userId, message, type, priority);
    }

    private void sendNotificationToBroker(
            UUID userId,
            String message,
            Notification.NotificationType type,
            Notification.NotificationPriority priority
    ) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("userId", userId);
        payload.put("message", message);
        payload.put("type", type);
        payload.put("priority", priority);

        logger.info("Sending notification to RabbitMQ. userId={}, type={}, priority={}",
                userId, type, priority);

        messageSender.send(
                payload,
                config.getExchangeName(),
                config.getRoutingKey()
        );
    }
}
