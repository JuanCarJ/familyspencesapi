package com.familyspencesapi.controllers.notification;

import com.familyspencesapi.domain.notification.Notification;
import com.familyspencesapi.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Obtiene todas las notificaciones
     * @return Lista de todas las notificaciones
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getAll() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    /**
     * Obtiene notificaciones por usuario
     * @param userId ID del usuario
     * @return Lista de notificaciones del usuario
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getByUserId(@PathVariable UUID userId) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene notificaciones no leídas por usuario
     * @param userId ID del usuario
     * @return Lista de notificaciones no leídas
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadByUserId(@PathVariable UUID userId) {
        try {
            List<Notification> notifications = notificationService.getUnreadNotificationsByUserId(userId);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene el conteo de notificaciones no leídas por usuario
     * @param userId ID del usuario
     * @return Cantidad de notificaciones no leídas
     */
    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable UUID userId) {
        try {
            long count = notificationService.getUnreadNotificationCount(userId);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene notificaciones recientes por usuario (últimas 24 horas)
     * @param userId ID del usuario
     * @return Lista de notificaciones recientes
     */
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<Notification>> getRecentByUserId(@PathVariable UUID userId) {
        try {
            List<Notification> notifications = notificationService.getRecentNotificationsByUserId(userId);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene notificaciones por usuario y tipo
     * @param userId ID del usuario
     * @param type Tipo de notificación
     * @return Lista de notificaciones del tipo especificado
     */
    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<List<Notification>> getByUserIdAndType(@PathVariable UUID userId,
                                                                 @PathVariable Notification.NotificationType type) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByUserIdAndType(userId, type);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene notificaciones por usuario y prioridad
     * @param userId ID del usuario
     * @param priority Prioridad de la notificación
     * @return Lista de notificaciones de la prioridad especificada
     */
    @GetMapping("/user/{userId}/priority/{priority}")
    public ResponseEntity<List<Notification>> getByUserIdAndPriority(@PathVariable UUID userId,
                                                                     @PathVariable Notification.NotificationPriority priority) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByUserIdAndPriority(userId, priority);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Crea una nueva notificación
     * @param notificationRequest Datos de la notificación
     * @return Notificación creada
     */
    @PostMapping
    public ResponseEntity<Notification> create(@RequestBody NotificationCreateRequest notificationRequest) {
        // Validación de campos requeridos
        if (notificationRequest.getMessage() == null || notificationRequest.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (notificationRequest.getUserId() == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // Crear notificación usando el service
            Notification notification = notificationService.createNotification(
                    notificationRequest.getUserId(),
                    notificationRequest.getMessage(),
                    notificationRequest.getType() != null ? notificationRequest.getType() : Notification.NotificationType.INFO,
                    notificationRequest.getPriority() != null ? notificationRequest.getPriority() : Notification.NotificationPriority.NORMAL
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(notification);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene una notificación por ID
     * @param id ID de la notificación
     * @return Notificación encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getById(@PathVariable UUID id) {
        Optional<Notification> notificationOpt = notificationService.getNotificationById(id);
        return notificationOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Marca una notificación como leída
     * @param id ID de la notificación
     * @return Notificación actualizada
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable UUID id) {
        boolean updated = notificationService.markNotificationAsRead(id);
        if (updated) {
            // Obtener la notificación actualizada
            Optional<Notification> notificationOpt = notificationService.getNotificationById(id);
            return notificationOpt.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Marca todas las notificaciones de un usuario como leídas
     * @param userId ID del usuario
     * @return Cantidad de notificaciones marcadas como leídas
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<NotificationBulkUpdateResponse> markAllAsReadByUser(@PathVariable UUID userId) {
        try {
            int updatedCount = notificationService.markAllNotificationsAsReadByUserId(userId);
            return ResponseEntity.ok(new NotificationBulkUpdateResponse(updatedCount, "Notificaciones marcadas como leídas"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Elimina una notificación
     * @param id ID de la notificación a eliminar
     * @return Respuesta sin contenido
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        boolean deleted = notificationService.deleteNotification(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Elimina todas las notificaciones leídas de un usuario
     * @param userId ID del usuario
     * @return Cantidad de notificaciones eliminadas
     */
    @DeleteMapping("/user/{userId}/read")
    public ResponseEntity<NotificationBulkUpdateResponse> deleteReadByUser(@PathVariable UUID userId) {
        try {
            int deletedCount = notificationService.deleteReadNotificationsByUserId(userId);
            return ResponseEntity.ok(new NotificationBulkUpdateResponse(deletedCount, "Notificaciones leídas eliminadas"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // =================== CLASES AUXILIARES ===================

    /**
     * DTO para crear notificaciones (evita exponer el constructor completo)
     */
    public static class NotificationCreateRequest {
        private UUID userId;
        private String message;
        private Notification.NotificationType type;
        private Notification.NotificationPriority priority;

        // Constructores
        public NotificationCreateRequest() {}

        public NotificationCreateRequest(UUID userId, String message) {
            this.userId = userId;
            this.message = message;
            this.type = Notification.NotificationType.INFO;
            this.priority = Notification.NotificationPriority.NORMAL;
        }

        // Getters y Setters
        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public Notification.NotificationType getType() { return type; }
        public void setType(Notification.NotificationType type) { this.type = type; }

        public Notification.NotificationPriority getPriority() { return priority; }
        public void setPriority(Notification.NotificationPriority priority) { this.priority = priority; }
    }

    /**
     * DTO para respuestas de operaciones masivas
     */
    public static class NotificationBulkUpdateResponse {
        private final int count;
        private final String message;

        public NotificationBulkUpdateResponse(int count, String message) {
            this.count = count;
            this.message = message;
        }

        // Getters
        public int getCount() { return count; }
        public String getMessage() { return message; }
    }
}