package com.recruitguard.backend.dto;

import java.util.List;

public class ScanResponse {
    private Integer postingId;
    private Integer riskScore;
    private String riskLevel;
    private String systemDecision;
    private List<FlagDetail> flaggedIndicators;

    public static class FlagDetail {
        private String indicatorType;
        private Integer points;
        private String description;

        public FlagDetail(String indicatorType, Integer points, String description) {
            this.indicatorType = indicatorType;
            this.points = points;
            this.description = description;
        }

        public String getIndicatorType() { return indicatorType; }
        public Integer getPoints() { return points; }
        public String getDescription() { return description; }
    }

    public Integer getPostingId() { return postingId; }
    public void setPostingId(Integer postingId) { this.postingId = postingId; }

    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getSystemDecision() { return systemDecision; }
    public void setSystemDecision(String systemDecision) { this.systemDecision = systemDecision; }

    public List<FlagDetail> getFlaggedIndicators() { return flaggedIndicators; }
    public void setFlaggedIndicators(List<FlagDetail> flaggedIndicators) { this.flaggedIndicators = flaggedIndicators; }
}