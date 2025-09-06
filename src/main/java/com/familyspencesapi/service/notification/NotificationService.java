package com.familyspencesapi.service.notification;

import com.familyspencesapi.domain.notification.Notification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class NotificationService {

    // Simulación de base de datos en memoria (temporal para desarrollo)
    private final List<Notification> notifications = new ArrayList<>();

    // UUIDs predefinidos para usuarios de prueba
    private static final UUID USER_1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private static final UUID USER_2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
    private static final UUID USER_3 = UUID.fromString("550e8400-e29b-41d4-a716-446655440003");

    public NotificationService() {
        initializeMockData();
    }

    /**
     * Inicializa datos de prueba
     */
    private void initializeMockData() {
        Date now = new Date();

        notifications.add(new Notification(
                UUID.randomUUID(), USER_1, "Bienvenido al sistema de gastos familiares",
                Notification.NotificationType.INFO, Notification.NotificationPriority.NORMAL, false,
                new Date(now.getTime() - 2 * 60 * 60 * 1000), null
        ));

        notifications.add(new Notification(
                UUID.randomUUID(), USER_1, "Tu presupuesto mensual está al 80%",
                Notification.NotificationType.WARNING, Notification.NotificationPriority.HIGH, true,
                new Date(now.getTime() - 5 * 60 * 60 * 1000),
                new Date(now.getTime() - 4 * 60 * 60 * 1000)
        ));

        notifications.add(new Notification(
                UUID.randomUUID(), USER_2, "Nuevo gasto registrado: $45.000 en Supermercado",
                Notification.NotificationType.EXPENSE_ADDED, Notification.NotificationPriority.NORMAL, false,
                new Date(now.getTime() - 60 * 60 * 1000), null
        ));

        notifications.add(new Notification(
                UUID.randomUUID(), USER_1, "Recordatorio: Pagar servicios públicos",
                Notification.NotificationType.PAYMENT_DUE, Notification.NotificationPriority.URGENT, false,
                new Date(now.getTime() - 30 * 60 * 1000), null
        ));

        notifications.add(new Notification(
                UUID.randomUUID(), USER_3, "Reporte mensual de gastos disponible",
                Notification.NotificationType.INFO, Notification.NotificationPriority.NORMAL, true,
                new Date(now.getTime() - 8 * 60 * 60 * 1000),
                new Date(now.getTime() - 7 * 60 * 60 * 1000)
        ));
    }

    /**
     * Obtiene todas las notificaciones
     * @return Lista de todas las notificaciones
     */
    public List<Notification> getAllNotifications() {
        return new ArrayList<>(notifications);
    }

    /**
     * Obtiene todas las notificaciones de un usuario
     * @param userId ID del usuario
     * @return Lista de notificaciones del usuario ordenadas por fecha de creación (más recientes primero)
     */
    public List<Notification> getNotificationsByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID de usuario no puede ser null");
        }

        return notifications.stream()
                .filter(notification -> notification.getUserId().equals(userId))
                .sorted((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt())) // Más recientes primero
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las notificaciones no leídas de un usuario
     * @param userId ID del usuario
     * @return Lista de notificaciones no leídas ordenadas por prioridad y fecha
     */
    public List<Notification> getUnreadNotificationsByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID de usuario no puede ser null");
        }

        return notifications.stream()
                .filter(notification -> notification.getUserId().equals(userId) && !notification.isRead())
                .sorted(this::compareByPriorityAndDate)
                .collect(Collectors.toList());
    }

    /**
     * Cuenta las notificaciones no leídas de un usuario
     * @param userId ID del usuario
     * @return Cantidad de notificaciones no leídas
     */
    public long getUnreadNotificationCount(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID de usuario no puede ser null");
        }

        return notifications.stream()
                .filter(notification -> notification.getUserId().equals(userId) && !notification.isRead())
                .count();
    }

    /**
     * Busca una notificación por ID
     * @param notificationId ID de la notificación
     * @return Optional con la notificación si existe
     */
    public Optional<Notification> getNotificationById(UUID notificationId) {
        if (notificationId == null) {
            return Optional.empty();
        }

        return notifications.stream()
                .filter(notification -> notification.getId().equals(notificationId))
                .findFirst();
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

        Notification notification = new Notification(
                UUID.randomUUID(),
                userId,
                message.trim(),
                type,
                priority,
                false,
                new Date(),
                null
        );

        notifications.add(notification);
        return notification;
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
        Optional<Notification> notificationOpt = getNotificationById(notificationId);

        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            if (!notification.isRead()) {
                notification.markAsRead();
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
        if (userId == null) {
            throw new IllegalArgumentException("El ID de usuario no puede ser null");
        }

        int markedCount = 0;
        Date now = new Date();

        for (Notification notification : notifications) {
            if (notification.getUserId().equals(userId) && !notification.isRead()) {
                notification.markAsRead();
                markedCount++;
            }
        }

        return markedCount;
    }

    /**
     * Elimina una notificación
     * @param notificationId ID de la notificación
     * @return true si se eliminó, false si no se encontró
     */
    public boolean deleteNotification(UUID notificationId) {
        return notifications.removeIf(notification -> notification.getId().equals(notificationId));
    }

    /**
     * Elimina todas las notificaciones leídas de un usuario
     * @param userId ID del usuario
     * @return Cantidad de notificaciones eliminadas
     */
    public int deleteReadNotificationsByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID de usuario no puede ser null");
        }

        int initialSize = notifications.size();
        notifications.removeIf(notification ->
                notification.getUserId().equals(userId) && notification.isRead());

        return initialSize - notifications.size();
    }

    /**
     * Obtiene notificaciones recientes de un usuario (últimas 24 horas)
     * @param userId ID del usuario
     * @return Lista de notificaciones recientes
     */
    public List<Notification> getRecentNotificationsByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID de usuario no puede ser null");
        }

        return notifications.stream()
                .filter(notification -> notification.getUserId().equals(userId) && notification.isRecent())
                .sorted((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene notificaciones por tipo para un usuario
     * @param userId ID del usuario
     * @param type Tipo de notificación
     * @return Lista de notificaciones del tipo especificado
     */
    public List<Notification> getNotificationsByUserIdAndType(UUID userId, Notification.NotificationType type) {
        if (userId == null || type == null) {
            throw new IllegalArgumentException("Los parámetros no pueden ser null");
        }

        return notifications.stream()
                .filter(notification -> notification.getUserId().equals(userId) && notification.getType() == type)
                .sorted((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene notificaciones por prioridad para un usuario
     * @param userId ID del usuario
     * @param priority Prioridad de la notificación
     * @return Lista de notificaciones de la prioridad especificada
     */
    public List<Notification> getNotificationsByUserIdAndPriority(UUID userId, Notification.NotificationPriority priority) {
        if (userId == null || priority == null) {
            throw new IllegalArgumentException("Los parámetros no pueden ser null");
        }

        return notifications.stream()
                .filter(notification -> notification.getUserId().equals(userId) && notification.getPriority() == priority)
                .sorted((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()))
                .collect(Collectors.toList());
    }

    // =================== MÉTODOS PRIVADOS ===================

    /**
     * Valida los parámetros para crear una notificación
     */
    private void validateCreateNotificationParams(UUID userId, String message,
                                                  Notification.NotificationType type,
                                                  Notification.NotificationPriority priority) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID de usuario no puede ser null");
        }
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

    /**
     * Compara notificaciones por prioridad y fecha
     * Prioridad: URGENT > HIGH > NORMAL > LOW
     * En caso de igual prioridad, ordena por fecha (más recientes primero)
     */
    private int compareByPriorityAndDate(Notification n1, Notification n2) {
        // Primero por prioridad (orden descendente)
        int priorityComparison = getPriorityWeight(n2.getPriority()) - getPriorityWeight(n1.getPriority());

        if (priorityComparison != 0) {
            return priorityComparison;
        }

        // Si tienen la misma prioridad, ordenar por fecha (más recientes primero)
        return n2.getCreatedAt().compareTo(n1.getCreatedAt());
    }

    /**
     * Obtiene el peso numérico de una prioridad para comparación
     */
    private int getPriorityWeight(Notification.NotificationPriority priority) {
        return switch (priority) {
            case URGENT -> 4;
            case HIGH -> 3;
            case NORMAL -> 2;
            case LOW -> 1;
        };
    }
}