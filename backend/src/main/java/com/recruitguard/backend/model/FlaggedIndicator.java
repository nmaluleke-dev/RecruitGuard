package com.recruitguard.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "flagged_indicators")
public class FlaggedIndicator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer indicatorId;

    @ManyToOne
    @JoinColumn(name = "score_id", nullable = false)
    private FraudScore fraudScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IndicatorType indicatorType;

    @Column(nullable = false)
    private Integer points;

    @Column(length = 255)
    private String description;

    public enum IndicatorType {
        suspicious_email,
        upfront_payment,
        unrealistic_salary,
        vague_description,
        missing_company_details,
        coercive_urgency
    }

    public Integer getIndicatorId() { return indicatorId; }
    public void setIndicatorId(Integer indicatorId) { this.indicatorId = indicatorId; }

    public FraudScore getFraudScore() { return fraudScore; }
    public void setFraudScore(FraudScore fraudScore) { this.fraudScore = fraudScore; }

    public IndicatorType getIndicatorType() { return indicatorType; }
    public void setIndicatorType(IndicatorType indicatorType) { this.indicatorType = indicatorType; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}