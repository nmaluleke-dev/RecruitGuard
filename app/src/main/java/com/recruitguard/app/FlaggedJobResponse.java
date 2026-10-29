package com.recruitguard.app;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FlaggedJobResponse {
    @SerializedName("postingId")
    private int postingId;

    @SerializedName("userId")
    private int userId;

    @SerializedName("contentPreview")
    private String contentPreview;

    @SerializedName("riskLevel")
    private String riskLevel;

    @SerializedName("riskScore")
    private int riskScore;

    @SerializedName("scanType")
    private String scanType;

    @SerializedName("submissionDate")
    private String submissionDate;

    @SerializedName("reviewed")
    private boolean reviewed;

    @SerializedName("reviewRequested")
    private boolean reviewRequested;

    @SerializedName("flaggedIndicators")
    private List<Indicator> flaggedIndicators;

    @SerializedName("fullContent")
    private String fullContent;

    public static class Indicator {
        @SerializedName("indicatorType")
        private String indicatorType;

        @SerializedName("points")
        private int points;

        @SerializedName("description")
        private String description;

        public String getIndicatorType() { return indicatorType; }
        public int getPoints() { return points; }
        public String getDescription() { return description; }
    }

    public int getPostingId() { return postingId; }
    public int getUserId() { return userId; }
    public String getContentPreview() { return contentPreview; }
    public String getRiskLevel() { return riskLevel; }
    public int getRiskScore() { return riskScore; }
    public String getScanType() { return scanType; }
    public String getSubmissionDate() { return submissionDate; }
    public boolean isReviewed() { return reviewed; }
    public boolean isReviewRequested() { return reviewRequested; }
    public List<Indicator> getFlaggedIndicators() { return flaggedIndicators; }
    public String getFullContent() { return fullContent; }

    public String getScanId() { return "CAS-" + postingId; }
    public String getUserName() { return "User #" + userId; }

    public String getJobTitle() {
        if (scanType != null && scanType.equalsIgnoreCase("url")) {
            String url = getScannedContent();
            if (url != null && url.contains("//")) {
                try {
                    String domain = url.split("//")[1].split("/")[0];
                    return domain.replace("www.", "");
                } catch (Exception e) {
                    return "URL Scan";
                }
            }
            return "URL Scan";
        }
        if (contentPreview != null && contentPreview.length() > 40) {
            return contentPreview.substring(0, 40) + "...";
        }
        return contentPreview != null ? contentPreview : "Job Posting";
    }

    public String getScannedContent() {
        return fullContent != null ? fullContent : contentPreview; }
    public String getSystemRisk() { return riskLevel != null ? riskLevel.replace("_", " ") : "unknown"; }
    public String getCreatedAt() { return submissionDate; }
}
