package com.recruitguard.app.jobseeker;


import com.google.gson.annotations.SerializedName;

public class ScanRequest
{
    @SerializedName("userId")
    private int userId;

    @SerializedName("scanType")
    private String scanType;

    @SerializedName("content")
    private String content;

    public ScanRequest(int userId, String scanType, String content)
    {
        this.userId = userId;
        this.scanType = scanType;
        this.content = content;
    }
}