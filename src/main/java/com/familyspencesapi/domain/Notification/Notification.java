package com.familyspencesapi.domain.Notification;

import java.time.LocalDateTime;

public class Notification {

    private Long id;
    private Long userId;          // usuario al que pertenece
    private String message;       // contenido de la notificación
    private boolean read;         // true si ya fue leída
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    // Constructor
    public Notification(Long id, Long userId, String message, boolean read,
                        LocalDateTime createdAt, LocalDateTime readAt) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.read = read;
        this.createdAt = createdAt;
        this.readAt = readAt;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
}
