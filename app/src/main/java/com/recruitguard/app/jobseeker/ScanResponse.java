package com.recruitguard.app;


import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ScanResponse
{
    @SerializedName("postingId")
    private int postingId;

    @SerializedName("riskScore")
    private int riskScore;

    @SerializedName("riskLevel")
    private String riskLevel;

    @SerializedName("flaggedIndicators")
    private List<Indicator> flaggedIndicators;

    public int getPostingId() { return postingId; }
    public int getRiskScore() { return riskScore; }
    public String getRiskLevel() { return riskLevel; }
    public List<Indicator> getFlaggedIndicators() { return flaggedIndicators; }

    public static class Indicator {
        @SerializedName("indicatorType")
        private String indicatorType;

        @SerializedName("description")
        private String description;

        @SerializedName("points")
        private int points;

        public String getIndicatorType() { return indicatorType; }
        public String getDescription() { return description; }
        public int getPoints() { return points; }
    }
}