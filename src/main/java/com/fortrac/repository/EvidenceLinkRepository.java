package com.fortrac.repository;

import com.fortrac.model.EvidenceLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceLinkRepository extends JpaRepository<EvidenceLink, Long> {
    List<EvidenceLink> findByCaseId(Long caseId);
    List<EvidenceLink> findBySourceArtifactIdOrTargetArtifactId(Long sourceId, Long targetId);
}
