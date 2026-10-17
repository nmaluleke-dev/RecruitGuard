package com.recruitguard.backend.dto;

public class ScanRequest {
    private Integer userId;
    private String scanType;
    private String content;

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getScanType() { return scanType; }
    public void setScanType(String scanType) { this.scanType = scanType; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}