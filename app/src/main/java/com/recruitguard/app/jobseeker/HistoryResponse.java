package com.recruitguard.app;

import com.google.gson.annotations.SerializedName;

public class HistoryResponse {
    @SerializedName("postingId")
    private int postingId;

    @SerializedName("contentPreview")
    private String contentPreview;

    @SerializedName("scanType")
    private String scanType;

    @SerializedName("riskScore")
    private int riskScore;

    @SerializedName("riskLevel")
    private String riskLevel;

    @SerializedName("submissionDate")
    private String submissionDate;

    @SerializedName("reviewed")
    private boolean reviewed;

    @SerializedName("reviewRequested")
    private boolean reviewRequested;

    @SerializedName("finalDecision")
    private String finalDecision;

    @SerializedName("recommendations")
    private String recommendations;

    @SerializedName("adminName")
    private String adminName;

    public int getPostingId() { return postingId; }
    public String getContentPreview() { return contentPreview; }
    public String getScanType() { return scanType; }
    public int getRiskScore() { return riskScore; }
    public String getRiskLevel() { return riskLevel; }
    public String getSubmissionDate() { return submissionDate; }
    public boolean isReviewed() { return reviewed; }
    public boolean isReviewRequested() { return reviewRequested; }
    public String getFinalDecision() { return finalDecision; }
    public String getRecommendations() { return recommendations; }
    public String getAdminName() { return adminName; }
}
