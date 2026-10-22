package com.recruitguard.app;

import com.google.gson.annotations.SerializedName;

public class FlaggedJobResponse
{
    @SerializedName("scanId")
    private String scanId;

    @SerializedName("userName")
    private String userName;

    @SerializedName("riskLevel")
    private String riskLevel;

    @SerializedName("scanType")
    private String scanType;

    @SerializedName("jobTitle")
    private String jobTitle;

    @SerializedName("scannedContent")
    private String scannedContent;

    @SerializedName("systemRisk")
    private String systemRisk;

    @SerializedName("riskScore")
    private int riskScore;

    @SerializedName("createdAt")
    private String createdAt;

    // Getters
    public String getScanId() { return scanId; }
    public String getUserName() { return userName; }
    public String getRiskLevel() { return riskLevel; }
    public String getScanType() { return scanType; }
    public String getJobTitle() { return jobTitle; }
    public String getScannedContent() { return scannedContent; }
    public String getSystemRisk() { return systemRisk; }
    public int getRiskScore() { return riskScore; }
    public String getCreatedAt() { return createdAt; }
}