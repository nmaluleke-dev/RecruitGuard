package com.recruitguard.app;

import com.google.gson.annotations.SerializedName;

public class ReviewDecisionRequest {
    @SerializedName("postingId")
    private int postingId;

    @SerializedName("adminId")
    private int adminId;

    @SerializedName("finalDecision")
    private String finalDecision;

    @SerializedName("riskScore")
    private int riskScore;

    @SerializedName("recommendations")
    private String recommendations;

    public ReviewDecisionRequest(int postingId, int adminId, String finalDecision, int riskScore, String recommendations) {
        this.postingId = postingId;
        this.adminId = adminId;
        this.finalDecision = finalDecision;
        this.riskScore = riskScore;
        this.recommendations = recommendations;
    }
}
