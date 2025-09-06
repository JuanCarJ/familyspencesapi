package com.familyspencesapi.domain.notification;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class Notification {

    private UUID id;
    private UUID userId;          // usuario al que pertenece
    private String message;       // contenido de la notificación
    private NotificationType type; // tipo de notificación (opcional)
    private NotificationPriority priority; // prioridad (opcional)
    private boolean read;         // true si ya fue leída
    private Date createdAt;       // fecha de creación
    private Date readAt;          // fecha de lectura

    // Constructor completo
    public Notification(UUID id, UUID userId, String message, NotificationType type,
                        NotificationPriority priority, boolean read,
                        Date createdAt, Date readAt) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.priority = priority;
        this.read = read;
        this.createdAt = createdAt;
        this.readAt = readAt;
    }

    // Constructor simplificado para crear nuevas notificaciones
    public Notification(UUID userId, String message, NotificationType type, NotificationPriority priority) {
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.priority = priority;
        this.read = false;
        this.createdAt = new Date();
        this.readAt = null;
    }

    // Constructor mínimo
    public Notification(UUID userId, String message) {
        this(userId, message, NotificationType.INFO, NotificationPriority.NORMAL);
    }

    // Método para marcar como leída
    public void markAsRead() {
        this.read = true;
        this.readAt = new Date();
    }

    // Método para verificar si la notificación es reciente (menos de 24 horas)
    public boolean isRecent() {
        if (createdAt == null) return false;
        long dayInMillis = 24 * 60 * 60 * 1000;
        return createdAt.getTime() > (System.currentTimeMillis() - dayInMillis);
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public NotificationPriority getPriority() { return priority; }
    public void setPriority(NotificationPriority priority) { this.priority = priority; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) {
        this.read = read;
        if (read && this.readAt == null) {
            this.readAt = new Date();
        }
    }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getReadAt() { return readAt; }
    public void setReadAt(Date readAt) { this.readAt = readAt; }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString
    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                ", type=" + type +
                ", priority=" + priority +
                ", read=" + read +
                ", createdAt=" + createdAt +
                ", readAt=" + readAt +
                '}';
    }

    // Enums para tipo y prioridad
    public enum NotificationType {
        INFO, WARNING, ERROR, SUCCESS, EXPENSE_ADDED, BUDGET_EXCEEDED, PAYMENT_DUE
    }

    public enum NotificationPriority {
        LOW, NORMAL, HIGH, URGENT
    }
}