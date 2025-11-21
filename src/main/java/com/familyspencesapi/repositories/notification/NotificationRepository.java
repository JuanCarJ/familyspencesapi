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

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId ORDER BY n.createdAtInternal DESC")
    List<Notification> findByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId);

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.read = false " +
            "ORDER BY CASE n.priority " +
            "WHEN 'URGENT' THEN 4 " +
            "WHEN 'HIGH' THEN 3 " +
            "WHEN 'NORMAL' THEN 2 " +
            "WHEN 'LOW' THEN 1 " +
            "END DESC, n.createdAtInternal DESC")
    List<Notification> findUnreadByUserIdOrderedByPriorityAndDate(@Param("userId") UUID userId);

    long countByUserIdAndReadFalse(UUID userId);

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.type = :type ORDER BY n.createdAtInternal DESC")
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(@Param("userId") UUID userId,
                                                               @Param("type") Notification.NotificationType type);

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.priority = :priority ORDER BY n.createdAtInternal DESC")
    List<Notification> findByUserIdAndPriorityOrderByCreatedAtDesc(@Param("userId") UUID userId,
                                                                   @Param("priority") Notification.NotificationPriority priority);

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.createdAtInternal > :since ORDER BY n.createdAtInternal DESC")
    List<Notification> findRecentNotificationsByUserId(@Param("userId") UUID userId, @Param("since") LocalDateTime since);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true, n.readAtInternal = :readAt WHERE n.userId = :userId AND n.read = false")
    int markAllAsReadByUserId(@Param("userId") UUID userId, @Param("readAt") LocalDateTime readAt);

    int deleteByUserIdAndReadTrue(UUID userId);
}
