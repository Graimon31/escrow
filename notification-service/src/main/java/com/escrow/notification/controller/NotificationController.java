package com.escrow.notification.controller;

import com.escrow.notification.dto.NotificationResponse;
import com.escrow.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationResponse> getNotifications(@RequestHeader("X-User-Id") String userId) {
        return notificationService.getUserNotifications(UUID.fromString(userId));
    }

    @GetMapping("/unread-count")
    public Map<String, Long> getUnreadCount(@RequestHeader("X-User-Id") String userId) {
        long count = notificationService.getUnreadCount(UUID.fromString(userId));
        return Map.of("count", count);
    }

    @PatchMapping("/{id}/read")
    public NotificationResponse markAsRead(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userId) {
        return notificationService.markAsRead(id, UUID.fromString(userId));
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@RequestHeader("X-User-Id") String userId) {
        notificationService.markAllAsRead(UUID.fromString(userId));
        return ResponseEntity.ok().build();
    }
}
