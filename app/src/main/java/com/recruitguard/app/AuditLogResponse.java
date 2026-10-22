package com.recruitguard.app;

import com.google.gson.annotations.SerializedName;

public class AuditLogResponse {
    @SerializedName("auditId")
    private int auditId;

    @SerializedName("postingId")
    private int postingId;

    @SerializedName("riskScore")
    private int riskScore;

    @SerializedName("finalDecision")
    private String finalDecision;

    @SerializedName("adminName")
    private String adminName;

    @SerializedName("timestamp")
    private String timestamp;

    public int getAuditId() { return auditId; }
    public int getPostingId() { return postingId; }
    public int getRiskScore() { return riskScore; }
    public String getFinalDecision() { return finalDecision; }
    public String getAdminName() { return adminName; }
    public String getTimestamp() { return timestamp; }

    public String getScanId() { return "CAS-" + postingId; }
    public String getDate() { return timestamp; }
    public String getDecision() { return finalDecision; }
    public String getRiskScoreText() { return riskScore + "%"; }
}
