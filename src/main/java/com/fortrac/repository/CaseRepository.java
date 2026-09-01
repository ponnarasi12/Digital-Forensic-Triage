package com.fortrac.repository;

import com.fortrac.model.ForensicCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<ForensicCase, Long> {
    Optional<ForensicCase> findByCaseNumber(String caseNumber);
}
