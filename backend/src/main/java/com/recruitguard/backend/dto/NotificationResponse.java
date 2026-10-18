package com.recruitguard.backend.dto;

import java.time.LocalDateTime;

public class NotificationResponse {
    private Integer notificationId;
    private Integer postingId;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public NotificationResponse(Integer notificationId, Integer postingId,
                                String message, Boolean isRead, LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.postingId = postingId;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public Integer getNotificationId() { return notificationId; }
    public Integer getPostingId() { return postingId; }
    public String getMessage() { return message; }
    public Boolean getIsRead() { return isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}