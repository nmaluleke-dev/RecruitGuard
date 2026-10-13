package com.recruitguard.backend.repository;

import com.recruitguard.backend.model.HumanReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HumanReviewRepository extends JpaRepository<HumanReview, Integer> {
    boolean existsByJobPostingPostingId(Integer postingId);
}