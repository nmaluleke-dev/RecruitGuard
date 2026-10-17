package com.recruitguard.backend.service;

import com.recruitguard.backend.dto.ScanRequest;
import com.recruitguard.backend.dto.ScanResponse;
import com.recruitguard.backend.model.*;
import com.recruitguard.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScanService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private FraudScoreRepository fraudScoreRepository;

    @Autowired
    private FlaggedIndicatorRepository flaggedIndicatorRepository;

    public ScanResponse scanJobPosting(ScanRequest request) {

        // Get user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Save job posting
        JobPosting posting = new JobPosting();
        posting.setUser(user);
        posting.setContentText(request.getContent());
        posting.setScanType(JobPosting.ScanType.valueOf(request.getScanType()));

        if (request.getScanType().equals("url")) {
            posting.setSourceUrl(request.getContent());
        }

        jobPostingRepository.save(posting);

        // Run fraud detection rules
        String content = request.getContent().toLowerCase();
        int totalScore = 0;
        List<ScanResponse.FlagDetail> flags = new ArrayList<>();
        List<FlaggedIndicator> indicators = new ArrayList<>();

        // Rule 1 — Suspicious email domain
        if (content.contains("@gmail.com") || content.contains("@yahoo.com") ||
                content.contains("@hotmail.com") || content.contains("@outlook.com")) {
            int points = 40;
            totalScore += points;
            flags.add(new ScanResponse.FlagDetail("suspicious_email", points,
                    "Free email domain used instead of corporate email"));
            FlaggedIndicator fi = new FlaggedIndicator();
            fi.setIndicatorType(FlaggedIndicator.IndicatorType.suspicious_email);
            fi.setPoints(points);
            fi.setDescription("Free email domain used instead of corporate email");
            indicators.add(fi);
        }

        // Rule 2 — Upfront payment request
        if (content.contains("pay") || content.contains("fee") ||
                content.contains("deposit") || content.contains("r500") ||
                content.contains("r1000") || content.contains("r2000") ||
                content.contains("payment required") || content.contains("upfront")) {
            int points = 40;
            totalScore += points;
            flags.add(new ScanResponse.FlagDetail("upfront_payment", points,
                    "Request for upfront payment or fees detected"));
            FlaggedIndicator fi = new FlaggedIndicator();
            fi.setIndicatorType(FlaggedIndicator.IndicatorType.upfront_payment);
            fi.setPoints(points);
            fi.setDescription("Request for upfront payment or fees detected");
            indicators.add(fi);
        }

        // Rule 3 — Unrealistic salary
        if (content.contains("r50000") || content.contains("r100000") ||
                content.contains("earn millions") || content.contains("unlimited income") ||
                content.contains("r500 per day") || content.contains("r1000 per day") ||
                content.contains("high salary") || content.contains("massive salary")) {
            int points = 20;
            totalScore += points;
            flags.add(new ScanResponse.FlagDetail("unrealistic_salary", points,
                    "Unrealistic salary or income claim detected"));
            FlaggedIndicator fi = new FlaggedIndicator();
            fi.setIndicatorType(FlaggedIndicator.IndicatorType.unrealistic_salary);
            fi.setPoints(points);
            fi.setDescription("Unrealistic salary or income claim detected");
            indicators.add(fi);
        }

        // Rule 4 — Vague description
        if (content.length() < 100 || content.contains("no experience needed") ||
                content.contains("no qualification") || content.contains("anyone can apply") ||
                content.contains("work from home and earn")) {
            int points = 10;
            totalScore += points;
            flags.add(new ScanResponse.FlagDetail("vague_description", points,
                    "Vague or minimal job description detected"));
            FlaggedIndicator fi = new FlaggedIndicator();
            fi.setIndicatorType(FlaggedIndicator.IndicatorType.vague_description);
            fi.setPoints(points);
            fi.setDescription("Vague or minimal job description detected");
            indicators.add(fi);
        }

        // Rule 5 — Missing company details
        if (!content.contains("pty") && !content.contains("ltd") &&
                !content.contains("company") && !content.contains("corporation") &&
                !content.contains("inc") && !content.contains("group")) {
            int points = 5;
            totalScore += points;
            flags.add(new ScanResponse.FlagDetail("missing_company_details", points,
                    "No company name or registration details found"));
            FlaggedIndicator fi = new FlaggedIndicator();
            fi.setIndicatorType(FlaggedIndicator.IndicatorType.missing_company_details);
            fi.setPoints(points);
            fi.setDescription("No company name or registration details found");
            indicators.add(fi);
        }

        // Rule 6 — Coercive urgency
        if (content.contains("urgent") || content.contains("apply now") ||
                content.contains("limited positions") || content.contains("act now") ||
                content.contains("today only") || content.contains("closing soon") ||
                content.contains("immediately")) {
            int points = 5;
            totalScore += points;
            flags.add(new ScanResponse.FlagDetail("coercive_urgency", points,
                    "Urgency language used to pressure applicants"));
            FlaggedIndicator fi = new FlaggedIndicator();
            fi.setIndicatorType(FlaggedIndicator.IndicatorType.coercive_urgency);
            fi.setPoints(points);
            fi.setDescription("Urgency language used to pressure applicants");
            indicators.add(fi);
        }

        // Cap score at 100
        if (totalScore > 100) totalScore = 100;

        // Determine risk level
        FraudScore.RiskLevel riskLevel = totalScore >= 50
                ? FraudScore.RiskLevel.high_risk
                : FraudScore.RiskLevel.low_risk;

        // Save fraud score
        FraudScore fraudScore = new FraudScore();
        fraudScore.setJobPosting(posting);
        fraudScore.setRiskScore(totalScore);
        fraudScore.setRiskLevel(riskLevel);
        fraudScore.setSystemDecision(riskLevel);
        fraudScoreRepository.save(fraudScore);

        // Save flagged indicators
        for (FlaggedIndicator fi : indicators) {
            fi.setFraudScore(fraudScore);
            flaggedIndicatorRepository.save(fi);
        }

        // Build response
        ScanResponse response = new ScanResponse();
        response.setPostingId(posting.getPostingId());
        response.setRiskScore(totalScore);
        response.setRiskLevel(riskLevel.name());
        response.setSystemDecision(riskLevel.name());
        response.setFlaggedIndicators(flags);

        return response;
    }
}