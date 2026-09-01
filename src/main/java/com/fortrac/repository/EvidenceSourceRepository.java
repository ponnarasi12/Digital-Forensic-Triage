package com.fortrac.repository;

import com.fortrac.model.EvidenceSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceSourceRepository extends JpaRepository<EvidenceSource, Long> {
    List<EvidenceSource> findByCaseId(Long caseId);
}
