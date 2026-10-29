package com.recruitguard.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ScanHistoryResponse {
    private Integer postingId;
    private String scanType;
    private String contentPreview;
    private Integer riskScore;
    private String riskLevel;
    private LocalDateTime submissionDate;
    private List<ScanResponse.FlagDetail> flaggedIndicators;

    // Human review details
    private boolean reviewRequested;
    private boolean reviewed;
    private String finalDecision;
    private String recommendations;

    public ScanHistoryResponse(Integer postingId, String scanType,
                               String contentPreview, Integer riskScore,
                               String riskLevel, LocalDateTime submissionDate,
                               List<ScanResponse.FlagDetail> flaggedIndicators) {
        this.postingId = postingId;
        this.scanType = scanType;
        this.contentPreview = contentPreview;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.submissionDate = submissionDate;
        this.flaggedIndicators = flaggedIndicators;
    }

    public Integer getPostingId() { return postingId; }
    public String getScanType() { return scanType; }
    public String getContentPreview() { return contentPreview; }
    public Integer getRiskScore() { return riskScore; }
    public String getRiskLevel() { return riskLevel; }
    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public List<ScanResponse.FlagDetail> getFlaggedIndicators() { return flaggedIndicators; }

    public boolean isReviewRequested() { return reviewRequested; }
    public void setReviewRequested(boolean reviewRequested) { this.reviewRequested = reviewRequested; }

    public boolean isReviewed() { return reviewed; }
    public void setReviewed(boolean reviewed) { this.reviewed = reviewed; }

    public String getFinalDecision() { return finalDecision; }
    public void setFinalDecision(String finalDecision) { this.finalDecision = finalDecision; }

    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
}