package com.recruitguard.app.jobseeker;


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

    public int getPostingId() { return postingId; }
    public String getContentPreview() { return contentPreview; }
    public String getScanType() { return scanType; }
    public int getRiskScore() { return riskScore; }
    public String getRiskLevel() { return riskLevel; }
    public String getSubmissionDate() { return submissionDate; }
}