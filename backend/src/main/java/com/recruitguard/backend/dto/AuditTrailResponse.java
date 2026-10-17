package com.recruitguard.backend.dto;

import java.time.LocalDateTime;

public class AuditTrailResponse {
    private Integer auditId;
    private Integer postingId;
    private Integer riskScore;
    private String finalDecision;
    private String adminName;
    private LocalDateTime timestamp;

    public AuditTrailResponse(Integer auditId, Integer postingId,
                              Integer riskScore, String finalDecision,
                              String adminName, LocalDateTime timestamp) {
        this.auditId = auditId;
        this.postingId = postingId;
        this.riskScore = riskScore;
        this.finalDecision = finalDecision;
        this.adminName = adminName;
        this.timestamp = timestamp;
    }

    public Integer getAuditId() { return auditId; }
    public Integer getPostingId() { return postingId; }
    public Integer getRiskScore() { return riskScore; }
    public String getFinalDecision() { return finalDecision; }
    public String getAdminName() { return adminName; }
    public LocalDateTime getTimestamp() { return timestamp; }
}