package com.hourlyhire.notification.service;

import com.hourlyhire.authentication.dto.PageResponse;
import com.hourlyhire.notification.dto.NotificationResponse;
import com.hourlyhire.notification.entity.Notification;
import com.hourlyhire.notification.enums.NotificationChannel;
import com.hourlyhire.notification.repository.NotificationRepository;
import com.hourlyhire.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class InAppNotificationService {

    private static final int MAX_PAGE_SIZE = 50;

    private final NotificationRepository repository;

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> list(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), MAX_PAGE_SIZE));

        return PageResponse.from(
                repository.findByUserIdAndChannelOrderByCreatedAtDesc(userId, NotificationChannel.IN_APP, pageable)
                        .map(NotificationResponse::from));
    }

    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {
        return repository.countByUserIdAndChannelAndReadAtIsNull(userId, NotificationChannel.IN_APP);
    }

    @Transactional
    public void markRead(Long userId, Long id) {
        // Scoped to the user and to IN_APP; anything else looks like "not found".
        Notification notification = repository.findByIdAndUserId(id, userId)
                .filter(n -> n.getChannel() == NotificationChannel.IN_APP)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", String.valueOf(id)));
        notification.markRead(Instant.now());
    }

    @Transactional
    public int markAllRead(Long userId) {
        return repository.markAllRead(userId, NotificationChannel.IN_APP, Instant.now());
    }
}