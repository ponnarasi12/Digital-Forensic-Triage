package com.fortrac.repository;

import com.fortrac.model.Artifact;
import com.fortrac.model.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtifactRepository extends JpaRepository<Artifact, Long> {

    List<Artifact> findByCaseId(Long caseId);

    List<Artifact> findByCaseIdOrderByRiskScoreDesc(Long caseId);

    List<Artifact> findByCaseIdAndRiskLevel(Long caseId, RiskLevel riskLevel);

    long countByCaseId(Long caseId);

    long countByCaseIdAndRiskLevel(Long caseId, RiskLevel riskLevel);

    @Query("SELECT a FROM Artifact a WHERE a.caseId = :caseId ORDER BY a.riskScore DESC")
    List<Artifact> findTopPriorityArtifacts(@Param("caseId") Long caseId);
}
