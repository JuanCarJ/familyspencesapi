package com.familyspencesapi.repositories.notification;

import com.familyspencesapi.domain.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    /**
     * Obtiene todas las notificaciones de un usuario ordenadas por fecha de creación descendente
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId ORDER BY n.createdAtInternal DESC")
    List<Notification> findByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId);

    /**
     * Obtiene las notificaciones no leídas de un usuario ordenadas por prioridad y fecha
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.read = false " +
            "ORDER BY CASE n.priority " +
            "WHEN 'URGENT' THEN 4 " +
            "WHEN 'HIGH' THEN 3 " +
            "WHEN 'NORMAL' THEN 2 " +
            "WHEN 'LOW' THEN 1 " +
            "END DESC, n.createdAtInternal DESC")
    List<Notification> findUnreadByUserIdOrderedByPriorityAndDate(@Param("userId") UUID userId);

    /**
     * Cuenta las notificaciones no leídas de un usuario
     */
    long countByUserIdAndReadFalse(UUID userId);

    /**
     * Obtiene notificaciones por usuario y tipo
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.type = :type ORDER BY n.createdAtInternal DESC")
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(@Param("userId") UUID userId,
                                                               @Param("type") Notification.NotificationType type);

    /**
     * Obtiene notificaciones por usuario y prioridad
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.priority = :priority ORDER BY n.createdAtInternal DESC")
    List<Notification> findByUserIdAndPriorityOrderByCreatedAtDesc(@Param("userId") UUID userId,
                                                                   @Param("priority") Notification.NotificationPriority priority);

    /**
     * Obtiene notificaciones recientes de un usuario (últimas 24 horas)
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.createdAtInternal > :since ORDER BY n.createdAtInternal DESC")
    List<Notification> findRecentNotificationsByUserId(@Param("userId") UUID userId, @Param("since") LocalDateTime since);

    /**
     * Marca todas las notificaciones no leídas de un usuario como leídas
     */
    @Modifying
    @Query("UPDATE Notification n SET n.read = true, n.readAtInternal = :readAt WHERE n.userId = :userId AND n.read = false")
    int markAllAsReadByUserId(@Param("userId") UUID userId, @Param("readAt") LocalDateTime readAt);

    /**
     * Elimina todas las notificaciones leídas de un usuario
     */
    int deleteByUserIdAndReadTrue(UUID userId);
}