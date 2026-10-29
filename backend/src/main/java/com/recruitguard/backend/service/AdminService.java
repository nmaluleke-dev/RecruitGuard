package com.recruitguard.backend.service;

import com.recruitguard.backend.dto.AuditTrailResponse;
import com.recruitguard.backend.dto.FlaggedPostingResponse;
import com.recruitguard.backend.dto.ReviewRequest;
import com.recruitguard.backend.dto.ScanResponse;
import com.recruitguard.backend.model.*;
import com.recruitguard.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private FraudScoreRepository fraudScoreRepository;

    @Autowired
    private FlaggedIndicatorRepository flaggedIndicatorRepository;

    @Autowired
    private HumanReviewRepository humanReviewRepository;

    @Autowired
    private AuditTrailRepository auditTrailRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    public List<FlaggedPostingResponse> getFlaggedPostings() {
        List<JobPosting> postings = jobPostingRepository.findAll();
        List<FlaggedPostingResponse> result = new ArrayList<>();

        for (JobPosting posting : postings) {
            // Only show postings where the user requested a review and no admin has reviewed them yet
            boolean reviewed = humanReviewRepository
                    .existsByJobPostingPostingId(posting.getPostingId());
            if (!posting.isReviewRequested() || reviewed) {
                continue;
            }

            FraudScore score = fraudScoreRepository
                    .findByJobPostingPostingId(posting.getPostingId());

            if (score != null) {
                FlaggedPostingResponse response = new FlaggedPostingResponse();
                response.setPostingId(posting.getPostingId());
                response.setUserId(posting.getUser().getUserId());
                response.setScanType(posting.getScanType().name());

                String preview = posting.getContentText() != null
                        ? posting.getContentText().substring(0,
                        Math.min(100, posting.getContentText().length())) + "..."
                        : posting.getSourceUrl();
                response.setContentPreview(preview);

                // Full posting text for the admin review screen
                response.setFullContent(posting.getContentText());

                response.setRiskScore(score.getRiskScore());
                response.setRiskLevel(score.getRiskLevel().name());
                response.setSubmissionDate(posting.getSubmissionDate());
                response.setReviewed(reviewed);
                response.setReviewRequested(posting.isReviewRequested());

                List<FlaggedIndicator> indicators = flaggedIndicatorRepository
                        .findByFraudScoreScoreId(score.getScoreId());
                List<ScanResponse.FlagDetail> flags = new ArrayList<>();
                for (FlaggedIndicator fi : indicators) {
                    flags.add(new ScanResponse.FlagDetail(
                            fi.getIndicatorType().name(),
                            fi.getPoints(),
                            fi.getDescription()));
                }
                response.setFlaggedIndicators(flags);
                result.add(response);
            }
        }
        return result;
    }

    public String submitReview(ReviewRequest request) {
        JobPosting posting = jobPostingRepository.findById(request.getPostingId())
                .orElseThrow(() -> new RuntimeException("Posting not found"));

        User admin = userRepository.findById(request.getAdminId())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        FraudScore score = fraudScoreRepository
                .findByJobPostingPostingId(posting.getPostingId());

        HumanReview review = new HumanReview();
        review.setJobPosting(posting);
        review.setAdmin(admin);
        review.setFinalDecision(HumanReview.Decision.valueOf(request.getFinalDecision()));
        review.setRecommendations(request.getRecommendations());
        humanReviewRepository.save(review);

        AuditTrail audit = new AuditTrail();
        audit.setJobPosting(posting);
        audit.setAdmin(admin);
        audit.setRiskScore(score != null ? score.getRiskScore() : 0);
        audit.setFinalDecision(HumanReview.Decision.valueOf(request.getFinalDecision()));
        audit.setAdminName(admin.getName());
        auditTrailRepository.save(audit);

        Notification notification = new Notification();
        notification.setUser(posting.getUser());
        notification.setJobPosting(posting);
        notification.setMessage("Your job posting has been reviewed. Please check your scan history for details");
        notificationRepository.save(notification);

        return "Review submitted successfully";
    }

    public List<AuditTrailResponse> getAuditTrail() {
        List<AuditTrail> audits = auditTrailRepository.findAllByOrderByTimestampDesc();
        List<AuditTrailResponse> result = new ArrayList<>();

        for (AuditTrail audit : audits) {
            result.add(new AuditTrailResponse(
                    audit.getAuditId(),
                    audit.getJobPosting().getPostingId(),
                    audit.getRiskScore(),
                    audit.getFinalDecision().name(),
                    audit.getAdminName(),
                    audit.getTimestamp()
            ));
        }
        return result;
    }
}