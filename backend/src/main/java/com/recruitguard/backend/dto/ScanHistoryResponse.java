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
}