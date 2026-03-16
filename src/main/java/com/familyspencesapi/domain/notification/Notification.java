package com.familyspencesapi.domain.notification;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "notifications", schema = "familyspences")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private NotificationPriority priority;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAtInternal;

    @Column(name = "read_at")
    private LocalDateTime readAtInternal;

    public Notification(UUID id, UUID userId, String message, NotificationType type,
                        NotificationPriority priority, boolean read,
                        Date createdAt, Date readAt) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.priority = priority;
        this.read = read;
        this.createdAtInternal = dateToLocalDateTime(createdAt);
        this.readAtInternal = dateToLocalDateTime(readAt);
    }

    public Notification(UUID userId, String message, NotificationType type, NotificationPriority priority) {
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.priority = priority;
        this.read = false;
        this.readAtInternal = null;
    }

    public Notification(UUID userId, String message) {
        this(userId, message, NotificationType.INFO, NotificationPriority.NORMAL);
    }

    public Notification() {}

    public void markAsRead() {
        this.read = true;
        this.readAtInternal = LocalDateTime.now();
    }

    public boolean isRecent() {
        if (createdAtInternal == null) return false;
        return createdAtInternal.isAfter(LocalDateTime.now().minusHours(24));
    }

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
        if (read && this.readAtInternal == null) {
            this.readAtInternal = LocalDateTime.now();
        }
    }

    public Date getCreatedAt() {
        return localDateTimeToDate(createdAtInternal);
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAtInternal = dateToLocalDateTime(createdAt);
    }

    public Date getReadAt() {
        return localDateTimeToDate(readAtInternal);
    }

    public void setReadAt(Date readAt) {
        this.readAtInternal = dateToLocalDateTime(readAt);
    }

    public LocalDateTime getCreatedAtInternal() { return createdAtInternal; }
    public void setCreatedAtInternal(LocalDateTime createdAtInternal) { this.createdAtInternal = createdAtInternal; }

    public LocalDateTime getReadAtInternal() { return readAtInternal; }
    public void setReadAtInternal(LocalDateTime readAtInternal) { this.readAtInternal = readAtInternal; }

    private Date localDateTimeToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private LocalDateTime dateToLocalDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

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

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                ", type=" + type +
                ", priority=" + priority +
                ", read=" + read +
                ", createdAt=" + getCreatedAt() +
                ", readAt=" + getReadAt() +
                '}';
    }

    public enum NotificationType {
        INFO, WARNING, ERROR, SUCCESS, EXPENSE_ADDED, BUDGET_EXCEEDED, PAYMENT_DUE
    }

    public enum NotificationPriority {
        LOW, NORMAL, HIGH, URGENT
    }
}
