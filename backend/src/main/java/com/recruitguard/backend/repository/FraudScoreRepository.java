package com.recruitguard.backend.repository;

import com.recruitguard.backend.model.FraudScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FraudScoreRepository extends JpaRepository<FraudScore, Integer> {
    FraudScore findByJobPostingPostingId(Integer postingId);
}