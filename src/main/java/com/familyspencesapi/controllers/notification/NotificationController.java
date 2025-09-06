package com.familyspencesapi.controllers.notification;

import com.familyspencesapi.domain.notification.Notification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controller temporal con datos quemados para desarrollo colaborativo
 * Adaptado al domain Notification específico del proyecto
 * TODO: Integrar con NotificationService cuando esté disponible
 *
 * @author Tu Nombre
 * @version 1.0
 * @since 2025-08-30
 */
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    // Simulación de base de datos en memoria para colaboración
    private static final List<Notification> NOTIFICATIONS = new ArrayList<>();

    // UUIDs predefinidos para los usuarios de prueba
    private static final UUID USER_1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private static final UUID USER_2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
    private static final UUID USER_3 = UUID.fromString("550e8400-e29b-41d4-a716-446655440003");

    // Datos quemados para demostración - adaptados al constructor con UUID y Date
    static {
        Date now = new Date();

        NOTIFICATIONS.add(new Notification(
                UUID.randomUUID(), USER_1, "Bienvenido al sistema de gastos familiares",
                Notification.NotificationType.INFO, Notification.NotificationPriority.NORMAL, false,
                new Date(now.getTime() - 2 * 60 * 60 * 1000), null // 2 horas atrás
        ));

        NOTIFICATIONS.add(new Notification(
                UUID.randomUUID(), USER_1, "Tu presupuesto mensual está al 80%",
                Notification.NotificationType.WARNING, Notification.NotificationPriority.HIGH, true,
                new Date(now.getTime() - 5 * 60 * 60 * 1000), // 5 horas atrás
                new Date(now.getTime() - 4 * 60 * 60 * 1000)  // leída hace 4 horas
        ));

        NOTIFICATIONS.add(new Notification(
                UUID.randomUUID(), USER_2, "Nuevo gasto registrado: $45.000 en Supermercado",
                Notification.NotificationType.EXPENSE_ADDED, Notification.NotificationPriority.NORMAL, false,
                new Date(now.getTime() - 60 * 60 * 1000), null // 1 hora atrás
        ));

        NOTIFICATIONS.add(new Notification(
                UUID.randomUUID(), USER_1, "Recordatorio: Pagar servicios públicos",
                Notification.NotificationType.PAYMENT_DUE, Notification.NotificationPriority.URGENT, false,
                new Date(now.getTime() - 30 * 60 * 1000), null // 30 minutos atrás
        ));

        NOTIFICATIONS.add(new Notification(
                UUID.randomUUID(), USER_3, "Reporte mensual de gastos disponible",
                Notification.NotificationType.INFO, Notification.NotificationPriority.NORMAL, true,
                new Date(now.getTime() - 8 * 60 * 60 * 1000), // 8 horas atrás
                new Date(now.getTime() - 7 * 60 * 60 * 1000)  // leída hace 7 horas
        ));

        NOTIFICATIONS.add(new Notification(
                UUID.randomUUID(), USER_2, "Meta de ahorro alcanzada: ¡Felicidades!",
                Notification.NotificationType.SUCCESS, Notification.NotificationPriority.NORMAL, false,
                new Date(now.getTime() - 15 * 60 * 1000), null // 15 minutos atrás
        ));
    }

    /**
     * Obtiene todas las notificaciones
     * @return Lista de todas las notificaciones
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getAll() {
        return ResponseEntity.ok(new ArrayList<>(NOTIFICATIONS));
    }

    /**
     * Obtiene notificaciones por usuario
     * @param userId ID del usuario
     * @return Lista de notificaciones del usuario
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getByUserId(@PathVariable UUID userId) {
        List<Notification> userNotifications = NOTIFICATIONS.stream()
                .filter(n -> n.getUserId().equals(userId))
                .toList();
        return ResponseEntity.ok(userNotifications);
    }

    /**
     * Obtiene notificaciones no leídas por usuario
     * @param userId ID del usuario
     * @return Lista de notificaciones no leídas
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadByUserId(@PathVariable UUID userId) {
        List<Notification> unreadNotifications = NOTIFICATIONS.stream()
                .filter(n -> n.getUserId().equals(userId) && !n.isRead())
                .toList();
        return ResponseEntity.ok(unreadNotifications);
    }

    /**
     * Obtiene el conteo de notificaciones no leídas por usuario
     * @param userId ID del usuario
     * @return Cantidad de notificaciones no leídas
     */
    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable UUID userId) {
        long count = NOTIFICATIONS.stream()
                .filter(n -> n.getUserId().equals(userId) && !n.isRead())
                .count();
        return ResponseEntity.ok(count);
    }

    /**
     * Crea una nueva notificación usando el constructor específico del domain
     * @param notificationRequest Datos de la notificación
     * @return Notificación creada
     */
    @PostMapping
    public ResponseEntity<Notification> create(@RequestBody NotificationCreateRequest notificationRequest) {
        // Validación básica
        if (notificationRequest.getMessage() == null || notificationRequest.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (notificationRequest.getUserId() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Crear nueva notificación usando el constructor específico del domain
        Notification notification = new Notification(
                UUID.randomUUID(),
                notificationRequest.getUserId(),
                notificationRequest.getMessage(),
                notificationRequest.getType() != null ? notificationRequest.getType() : Notification.NotificationType.INFO,
                notificationRequest.getPriority() != null ? notificationRequest.getPriority() : Notification.NotificationPriority.NORMAL,
                false, // Nueva notificación siempre no leída
                new Date(), // Fecha de creación actual
                null // readAt es null hasta que se marque como leída
        );

        // Agregar a la lista
        NOTIFICATIONS.add(notification);

        return ResponseEntity.status(HttpStatus.CREATED).body(notification);
    }

    /**
     * Obtiene una notificación por ID
     * @param id ID de la notificación
     * @return Notificación encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getById(@PathVariable UUID id) {
        return NOTIFICATIONS.stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Marca una notificación como leída
     * @param id ID de la notificación
     * @return Notificación actualizada
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable UUID id) {
        return NOTIFICATIONS.stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .map(notification -> {
                    // Usar el método markAsRead() del domain
                    notification.markAsRead();
                    return ResponseEntity.ok(notification);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Marca todas las notificaciones de un usuario como leídas
     * @param userId ID del usuario
     * @return Cantidad de notificaciones marcadas como leídas
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<NotificationBulkUpdateResponse> markAllAsReadByUser(@PathVariable UUID userId) {
        int updatedCount = 0;

        for (Notification notification : NOTIFICATIONS) {
            if (notification.getUserId().equals(userId) && !notification.isRead()) {
                notification.markAsRead();
                updatedCount++;
            }
        }

        return ResponseEntity.ok(new NotificationBulkUpdateResponse(updatedCount, "Notificaciones marcadas como leídas"));
    }

    /**
     * Elimina una notificación
     * @param id ID de la notificación a eliminar
     * @return Respuesta sin contenido
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        boolean removed = NOTIFICATIONS.removeIf(n -> n.getId().equals(id));
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Elimina todas las notificaciones leídas de un usuario
     * @param userId ID del usuario
     * @return Cantidad de notificaciones eliminadas
     */
    @DeleteMapping("/user/{userId}/read")
    public ResponseEntity<NotificationBulkUpdateResponse> deleteReadByUser(@PathVariable UUID userId) {
        int initialSize = NOTIFICATIONS.size();
        NOTIFICATIONS.removeIf(n -> n.getUserId().equals(userId) && n.isRead());
        int deletedCount = initialSize - NOTIFICATIONS.size();

        return ResponseEntity.ok(new NotificationBulkUpdateResponse(deletedCount, "Notificaciones leídas eliminadas"));
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
        private int count;
        private String message;

        public NotificationBulkUpdateResponse(int count, String message) {
            this.count = count;
            this.message = message;
        }

        // Getters
        public int getCount() { return count; }
        public String getMessage() { return message; }
    }
}