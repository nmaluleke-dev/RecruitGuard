package com.recruitguard.backend.service;

import com.recruitguard.backend.dto.ScanHistoryResponse;
import com.recruitguard.backend.model.FraudScore;
import com.recruitguard.backend.model.JobPosting;
import com.recruitguard.backend.repository.FraudScoreRepository;
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
                history.add(new ScanHistoryResponse(
                        posting.getPostingId(),
                        posting.getScanType().name(),
                        preview,
                        score.getRiskScore(),
                        score.getRiskLevel().name(),
                        posting.getSubmissionDate()
                ));
            }
        }
        return history;
    }
}