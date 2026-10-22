package com.recruitguard.app.jobseeker;


import com.google.gson.annotations.SerializedName;

public class AuditLogResponse {
    @SerializedName("scanId")
    private String scanId;

    @SerializedName("date")
    private String date;

    @SerializedName("riskScore")
    private String riskScore;

    @SerializedName("decision")
    private String decision;

    @SerializedName("adminName")
    private String adminName;

    @SerializedName("timestamp")
    private String timestamp;

    // Getters
    public String getScanId() { return scanId; }
    public String getDate() { return date; }
    public String getRiskScore() { return riskScore; }
    public String getDecision() { return decision; }
    public String getAdminName() { return adminName; }
    public String getTimestamp() { return timestamp; }
}