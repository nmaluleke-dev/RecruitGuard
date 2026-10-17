package com.recruitguard.backend.repository;

import com.recruitguard.backend.model.AuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, Integer> {
    List<AuditTrail> findAllByOrderByTimestampDesc();
}