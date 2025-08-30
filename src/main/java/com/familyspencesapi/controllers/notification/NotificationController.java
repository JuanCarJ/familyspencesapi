package com.familyspencesapi.controllers.notification;

import com.familyspencesapi.domain.notification.Notification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    // Datos quemados para demostración - adaptados al constructor específico
    static {
        NOTIFICATIONS.add(new Notification(
                1L, 1001L, "Bienvenido al sistema de gastos familiares", false,
                LocalDateTime.now().minusHours(2), null
        ));
        NOTIFICATIONS.add(new Notification(
                2L, 1001L, "Tu presupuesto mensual está al 80%", true,
                LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(4)
        ));
        NOTIFICATIONS.add(new Notification(
                3L, 1002L, "Nuevo gasto registrado: $45.000 en Supermercado", false,
                LocalDateTime.now().minusHours(1), null
        ));
        NOTIFICATIONS.add(new Notification(
                4L, 1001L, "Recordatorio: Pagar servicios públicos", false,
                LocalDateTime.now().minusMinutes(30), null
        ));
        NOTIFICATIONS.add(new Notification(
                5L, 1003L, "Reporte mensual de gastos disponible", true,
                LocalDateTime.now().minusHours(8), LocalDateTime.now().minusHours(7)
        ));
        NOTIFICATIONS.add(new Notification(
                6L, 1002L, "Meta de ahorro alcanzada: ¡Felicidades!", false,
                LocalDateTime.now().minusMinutes(15), null
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
    public ResponseEntity<List<Notification>> getByUserId(@PathVariable Long userId) {
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
    public ResponseEntity<List<Notification>> getUnreadByUserId(@PathVariable Long userId) {
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
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long userId) {
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
        Long newId = ID_GENERATOR.getAndIncrement();
        Notification notification = new Notification(
                newId,
                notificationRequest.getUserId(),
                notificationRequest.getMessage(),
                false, // Nueva notificación siempre no leída
                LocalDateTime.now(), // Fecha de creación actual
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
    public ResponseEntity<Notification> getById(@PathVariable Long id) {
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
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        return NOTIFICATIONS.stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .map(notification -> {
                    // Crear nueva instancia con estado actualizado (inmutable approach)
                    Notification updatedNotification = new Notification(
                            notification.getId(),
                            notification.getUserId(),
                            notification.getMessage(),
                            true, // Marcada como leída
                            notification.getCreatedAt(),
                            LocalDateTime.now() // Fecha de lectura actual
                    );

                    // Reemplazar en la lista
                    int index = NOTIFICATIONS.indexOf(notification);
                    NOTIFICATIONS.set(index, updatedNotification);

                    return ResponseEntity.ok(updatedNotification);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Marca todas las notificaciones de un usuario como leídas
     * @param userId ID del usuario
     * @return Cantidad de notificaciones marcadas como leídas
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<NotificationBulkUpdateResponse> markAllAsReadByUser(@PathVariable Long userId) {
        LocalDateTime now = LocalDateTime.now();
        int updatedCount = 0;

        for (int i = 0; i < NOTIFICATIONS.size(); i++) {
            Notification notification = NOTIFICATIONS.get(i);
            if (notification.getUserId().equals(userId) && !notification.isRead()) {
                // Crear versión actualizada
                Notification updatedNotification = new Notification(
                        notification.getId(),
                        notification.getUserId(),
                        notification.getMessage(),
                        true,
                        notification.getCreatedAt(),
                        now
                );
                NOTIFICATIONS.set(i, updatedNotification);
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
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean removed = NOTIFICATIONS.removeIf(n -> n.getId().equals(id));
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Elimina todas las notificaciones leídas de un usuario
     * @param userId ID del usuario
     * @return Cantidad de notificaciones eliminadas
     */
    @DeleteMapping("/user/{userId}/read")
    public ResponseEntity<NotificationBulkUpdateResponse> deleteReadByUser(@PathVariable Long userId) {
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
        private Long userId;
        private String message;

        // Constructores
        public NotificationCreateRequest() {}

        public NotificationCreateRequest(Long userId, String message) {
            this.userId = userId;
            this.message = message;
        }

        // Getters y Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
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
