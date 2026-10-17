package com.recruitguard.backend.controller;

import com.recruitguard.backend.dto.NotificationResponse;
import com.recruitguard.backend.model.Notification;
import com.recruitguard.backend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserNotifications(@PathVariable Integer userId) {
        try {
            List<Notification> notifications = notificationRepository
                    .findByUserUserId(userId);
            List<NotificationResponse> result = new ArrayList<>();
            for (Notification n : notifications) {
                result.add(new NotificationResponse(
                        n.getNotificationId(),
                        n.getJobPosting().getPostingId(),
                        n.getMessage(),
                        n.getIsRead(),
                        n.getCreatedAt()
                ));
            }
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}