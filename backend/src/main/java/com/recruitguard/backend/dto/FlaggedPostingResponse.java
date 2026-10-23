package com.recruitguard.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class FlaggedPostingResponse {
    private Integer postingId;
    private Integer userId;
    private String scanType;
    private String contentPreview;
    private Integer riskScore;
    private String riskLevel;
    private LocalDateTime submissionDate;
    private boolean reviewed;
    private List<ScanResponse.FlagDetail> flaggedIndicators;

    private String fullContent;

    public String getFullContent() { return fullContent; }
    public void setFullContent(String fullContent) { this.fullContent = fullContent; }

    public Integer getPostingId() { return postingId; }
    public void setPostingId(Integer postingId) { this.postingId = postingId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getScanType() { return scanType; }
    public void setScanType(String scanType) { this.scanType = scanType; }

    public String getContentPreview() { return contentPreview; }
    public void setContentPreview(String contentPreview) { this.contentPreview = contentPreview; }

    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate; }

    public boolean isReviewed() { return reviewed; }
    public void setReviewed(boolean reviewed) { this.reviewed = reviewed; }

    public List<ScanResponse.FlagDetail> getFlaggedIndicators() { return flaggedIndicators; }
    public void setFlaggedIndicators(List<ScanResponse.FlagDetail> flaggedIndicators) { this.flaggedIndicators = flaggedIndicators; }
}