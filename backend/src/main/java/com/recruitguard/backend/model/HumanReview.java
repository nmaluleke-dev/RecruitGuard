package com.recruitguard.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "human_reviews")
public class HumanReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reviewId;

    @OneToOne
    @JoinColumn(name = "posting_id", nullable = false)
    private JobPosting jobPosting;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Decision finalDecision;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @Column(updatable = false)
    private LocalDateTime reviewedAt = LocalDateTime.now();

    public enum Decision {
        low_risk, high_risk
    }

    public Integer getReviewId() { return reviewId; }
    public void setReviewId(Integer reviewId) { this.reviewId = reviewId; }

    public JobPosting getJobPosting() { return jobPosting; }
    public void setJobPosting(JobPosting jobPosting) { this.jobPosting = jobPosting; }

    public User getAdmin() { return admin; }
    public void setAdmin(User admin) { this.admin = admin; }

    public Decision getFinalDecision() { return finalDecision; }
    public void setFinalDecision(Decision finalDecision) { this.finalDecision = finalDecision; }

    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}