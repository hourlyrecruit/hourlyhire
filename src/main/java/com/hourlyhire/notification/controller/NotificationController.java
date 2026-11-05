package com.hourlyhire.notification.controller;

import com.hourlyhire.authentication.dto.PageResponse;
import com.hourlyhire.notification.dto.NotificationResponse;
import com.hourlyhire.notification.service.InAppNotificationService;
import com.hourlyhire.security.jwt.AuthenticatedUser;
import com.hourlyhire.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "03-NotificationController", description = "In-app notification APIs")
@RequiredArgsConstructor
public class NotificationController {

    private final InAppNotificationService service;

    @GetMapping
    @Operation(summary = "List my in-app notifications (paged, newest first). Access: Authenticated",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> list(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {

        return ResponseEntity.ok(ApiResponse.success(
                "Notifications retrieved successfully.",
                service.list(user.id(), page, size),
                httpRequest.getRequestURI()));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Number of unread notifications. Access: Authenticated",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Map<String, Long>>> unreadCount(
            @AuthenticationPrincipal AuthenticatedUser user,
            HttpServletRequest httpRequest) {

        return ResponseEntity.ok(ApiResponse.success(
                "Unread count retrieved successfully.",
                Map.of("count", service.unreadCount(user.id())),
                httpRequest.getRequestURI()));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark one notification as read. Access: Authenticated",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> markRead(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        service.markRead(user.id(), id);
        return ResponseEntity.ok(ApiResponse.success(
                "Notification marked as read.", null, httpRequest.getRequestURI()));
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark all my notifications as read. Access: Authenticated",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Map<String, Integer>>> markAllRead(
            @AuthenticationPrincipal AuthenticatedUser user,
            HttpServletRequest httpRequest) {

        return ResponseEntity.ok(ApiResponse.success(
                "All notifications marked as read.",
                Map.of("updated", service.markAllRead(user.id())),
                httpRequest.getRequestURI()));
    }
}