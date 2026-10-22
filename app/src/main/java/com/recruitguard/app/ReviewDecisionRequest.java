package com.recruitguard.app;

import com.google.gson.annotations.SerializedName;

public class ReviewDecisionRequest {
    @SerializedName("scanId")
    private String scanId;

    @SerializedName("decision")
    private String decision;

    @SerializedName("adminName")
    private String adminName;

    @SerializedName("adminId")
    private int adminId;

    public ReviewDecisionRequest(String scanId, String decision, String adminName, int adminId) {
        this.scanId = scanId;
        this.decision = decision;
        this.adminName = adminName;
        this.adminId = adminId;
    }
}