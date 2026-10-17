package com.recruitguard.backend.dto;

public class ReviewRequest {
    private Integer postingId;
    private Integer adminId;
    private String finalDecision;
    private String recommendations;

    public Integer getPostingId() { return postingId; }
    public void setPostingId(Integer postingId) { this.postingId = postingId; }

    public Integer getAdminId() { return adminId; }
    public void setAdminId(Integer adminId) { this.adminId = adminId; }

    public String getFinalDecision() { return finalDecision; }
    public void setFinalDecision(String finalDecision) { this.finalDecision = finalDecision; }

    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
}