package com.familyspencesapi.service.notification;

import com.familyspencesapi.domain.notification.Notification;
import com.familyspencesapi.repositories.notification.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Obtiene todas las notificaciones
     * @return Lista de todas las notificaciones
     */
    @Transactional(readOnly = true)
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    /**
     * Obtiene todas las notificaciones de un usuario
     * @param userId ID del usuario
     * @return Lista de notificaciones del usuario ordenadas por fecha de creación (más recientes primero)
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUserId(UUID userId) {
        validateUserId(userId);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Obtiene las notificaciones no leídas de un usuario
     * @param userId ID del usuario
     * @return Lista de notificaciones no leídas ordenadas por prioridad y fecha
     */
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotificationsByUserId(UUID userId) {
        validateUserId(userId);
        return notificationRepository.findUnreadByUserIdOrderedByPriorityAndDate(userId);
    }

    /**
     * Cuenta las notificaciones no leídas de un usuario
     * @param userId ID del usuario
     * @return Cantidad de notificaciones no leídas
     */
    @Transactional(readOnly = true)
    public long getUnreadNotificationCount(UUID userId) {
        validateUserId(userId);
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    /**
     * Busca una notificación por ID
     * @param notificationId ID de la notificación
     * @return Optional con la notificación si existe
     */
    @Transactional(readOnly = true)
    public Optional<Notification> getNotificationById(UUID notificationId) {
        if (notificationId == null) {
            return Optional.empty();
        }
        return notificationRepository.findById(notificationId);
    }

    /**
     * Crea una nueva notificación
     * @param userId ID del usuario destinatario
     * @param message Mensaje de la notificación
     * @param type Tipo de notificación
     * @param priority Prioridad de la notificación
     * @return La notificación creada
     */
    public Notification createNotification(UUID userId, String message,
                                           Notification.NotificationType type,
                                           Notification.NotificationPriority priority) {
        validateCreateNotificationParams(userId, message, type, priority);

        // Crear nueva notificación usando el constructor del domain
        Notification notification = new Notification(userId, message.trim(), type, priority);

        // Guardar en base de datos
        return notificationRepository.save(notification);
    }

    /**
     * Crea una nueva notificación con valores por defecto
     * @param userId ID del usuario destinatario
     * @param message Mensaje de la notificación
     * @return La notificación creada
     */
    public Notification createNotification(UUID userId, String message) {
        return createNotification(userId, message,
                Notification.NotificationType.INFO,
                Notification.NotificationPriority.NORMAL);
    }

    /**
     * Marca una notificación como leída
     * @param notificationId ID de la notificación
     * @return true si se marcó como leída, false si no se encontró
     */
    public boolean markNotificationAsRead(UUID notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);

        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            if (!notification.isRead()) {
                notification.markAsRead();
                notificationRepository.save(notification);
                return true;
            }
        }

        return false;
    }

    /**
     * Marca todas las notificaciones de un usuario como leídas
     * @param userId ID del usuario
     * @return Cantidad de notificaciones marcadas como leídas
     */
    public int markAllNotificationsAsReadByUserId(UUID userId) {
        validateUserId(userId);
        return notificationRepository.markAllAsReadByUserId(userId, LocalDateTime.now());
    }

    /**
     * Elimina una notificación
     * @param notificationId ID de la notificación
     * @return true si se eliminó, false si no se encontró
     */
    public boolean deleteNotification(UUID notificationId) {
        if (notificationId == null) {
            return false;
        }

        if (notificationRepository.existsById(notificationId)) {
            notificationRepository.deleteById(notificationId);
            return true;
        }

        return false;
    }

    /**
     * Elimina todas las notificaciones leídas de un usuario
     * @param userId ID del usuario
     * @return Cantidad de notificaciones eliminadas
     */
    public int deleteReadNotificationsByUserId(UUID userId) {
        validateUserId(userId);
        return notificationRepository.deleteByUserIdAndReadTrue(userId);
    }

    /**
     * Obtiene notificaciones recientes de un usuario (últimas 24 horas)
     * @param userId ID del usuario
     * @return Lista de notificaciones recientes
     */
    @Transactional(readOnly = true)
    public List<Notification> getRecentNotificationsByUserId(UUID userId) {
        validateUserId(userId);
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return notificationRepository.findRecentNotificationsByUserId(userId, since);
    }

    /**
     * Obtiene notificaciones por tipo para un usuario
     * @param userId ID del usuario
     * @param type Tipo de notificación
     * @return Lista de notificaciones del tipo especificado
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUserIdAndType(UUID userId, Notification.NotificationType type) {
        validateUserId(userId);
        if (type == null) {
            throw new IllegalArgumentException("El tipo no puede ser null");
        }
        return notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type);
    }

    /**
     * Obtiene notificaciones por prioridad para un usuario
     * @param userId ID del usuario
     * @param priority Prioridad de la notificación
     * @return Lista de notificaciones de la prioridad especificada
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUserIdAndPriority(UUID userId, Notification.NotificationPriority priority) {
        validateUserId(userId);
        if (priority == null) {
            throw new IllegalArgumentException("La prioridad no puede ser null");
        }
        return notificationRepository.findByUserIdAndPriorityOrderByCreatedAtDesc(userId, priority);
    }

    // =================== MÉTODOS PRIVADOS ===================

    /**
     * Valida que el userId no sea null
     */
    private void validateUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID de usuario no puede ser null");
        }
    }

    /**
     * Valida los parámetros para crear una notificación
     */
    private void validateCreateNotificationParams(UUID userId, String message,
                                                  Notification.NotificationType type,
                                                  Notification.NotificationPriority priority) {
        validateUserId(userId);
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("El mensaje no puede ser null o vacío");
        }
        if (type == null) {
            throw new IllegalArgumentException("El tipo de notificación no puede ser null");
        }
        if (priority == null) {
            throw new IllegalArgumentException("La prioridad no puede ser null");
        }
    }
}