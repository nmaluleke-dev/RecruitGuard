package com.recruitguard.backend.service;

import com.recruitguard.backend.dto.ScanHistoryResponse;
import com.recruitguard.backend.dto.ScanResponse;
import com.recruitguard.backend.model.FlaggedIndicator;
import com.recruitguard.backend.model.FraudScore;
import com.recruitguard.backend.model.HumanReview;
import com.recruitguard.backend.model.JobPosting;
import com.recruitguard.backend.repository.FlaggedIndicatorRepository;
import com.recruitguard.backend.repository.FraudScoreRepository;
import com.recruitguard.backend.repository.HumanReviewRepository;
import com.recruitguard.backend.repository.JobPostingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class HistoryService {

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private FraudScoreRepository fraudScoreRepository;

    @Autowired
    private FlaggedIndicatorRepository flaggedIndicatorRepository;

    @Autowired
    private HumanReviewRepository humanReviewRepository;

    public List<ScanHistoryResponse> getUserHistory(Integer userId) {
        List<JobPosting> postings = jobPostingRepository.findByUserUserId(userId);
        List<ScanHistoryResponse> history = new ArrayList<>();

        for (JobPosting posting : postings) {
            FraudScore score = fraudScoreRepository
                    .findByJobPostingPostingId(posting.getPostingId());

            String preview = posting.getContentText() != null
                    ? posting.getContentText().substring(0,
                    Math.min(100, posting.getContentText().length())) + "..."
                    : posting.getSourceUrl();

            if (score != null) {
                List<FlaggedIndicator> indicators = flaggedIndicatorRepository
                        .findByFraudScoreScoreId(score.getScoreId());
                List<ScanResponse.FlagDetail> flags = new ArrayList<>();
                for (FlaggedIndicator fi : indicators) {
                    flags.add(new ScanResponse.FlagDetail(
                            fi.getIndicatorType().name(),
                            fi.getPoints(),
                            fi.getDescription()));
                }

                ScanHistoryResponse item = new ScanHistoryResponse(
                        posting.getPostingId(),
                        posting.getScanType().name(),
                        preview,
                        score.getRiskScore(),
                        score.getRiskLevel().name(),
                        posting.getSubmissionDate(),
                        flags
                );

                // Human review details, if an admin has reviewed this posting
                item.setReviewRequested(posting.isReviewRequested());
                List<HumanReview> reviews = humanReviewRepository
                        .findByJobPostingPostingId(posting.getPostingId());
                if (!reviews.isEmpty()) {
                    HumanReview review = reviews.get(reviews.size() - 1);
                    item.setReviewed(true);
                    item.setFinalDecision(review.getFinalDecision().name());
                    item.setRecommendations(review.getRecommendations());
                } else {
                    item.setReviewed(false);
                }

                history.add(item);
            }
        }
        return history;
    }
}