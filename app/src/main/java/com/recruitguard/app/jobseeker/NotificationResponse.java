package com.recruitguard.app.jobseeker;


import com.google.gson.annotations.SerializedName;

public class NotificationResponse {
    @SerializedName("notificationId")
    private int notificationId;

    @SerializedName("postingId")
    private int postingId;

    @SerializedName("message")
    private String message;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("isRead")
    private boolean isRead;

    public int getNotificationId() { return notificationId; }
    public int getPostingId() { return postingId; }
    public String getMessage() { return message; }
    public String getCreatedAt() { return createdAt; }
    public boolean isRead() { return isRead; }
}