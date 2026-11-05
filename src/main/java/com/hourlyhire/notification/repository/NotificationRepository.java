package com.hourlyhire.notification.repository;

import com.hourlyhire.notification.entity.Notification;
import com.hourlyhire.notification.enums.NotificationChannel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Idempotency check: has this event already been handled for this channel?
    boolean existsByEventIdAndChannel(UUID eventId, NotificationChannel channel);

    Page<Notification> findByUserIdAndChannelOrderByCreatedAtDesc(
            Long userId, NotificationChannel channel, Pageable pageable);

    long countByUserIdAndChannelAndReadAtIsNull(Long userId, NotificationChannel channel);

    // Scoped to the user, so one user can never open another user's notification.
    Optional<Notification> findByIdAndUserId(Long id, Long userId);

    @Modifying
    @Query("update Notification n set n.readAt = :now, n.updatedAt = :now "
            + "where n.userId = :userId and n.channel = :channel and n.readAt is null")
    int markAllRead(@Param("userId") Long userId,
                    @Param("channel") NotificationChannel channel,
                    @Param("now") Instant now);

    Optional<Notification> findByEventIdAndChannel(UUID eventId, NotificationChannel channel);
}