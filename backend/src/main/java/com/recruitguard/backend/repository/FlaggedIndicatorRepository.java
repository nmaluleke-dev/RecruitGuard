package com.recruitguard.backend.repository;

import com.recruitguard.backend.model.FlaggedIndicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FlaggedIndicatorRepository extends JpaRepository<FlaggedIndicator, Integer> {
    List<FlaggedIndicator> findByFraudScoreScoreId(Integer scoreId);
}