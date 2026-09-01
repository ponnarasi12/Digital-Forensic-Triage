package com.fortrac.repository;

import com.fortrac.model.TimelineEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimelineEventRepository extends JpaRepository<TimelineEvent, Long> {
    List<TimelineEvent> findByCaseIdOrderByEventTimeAsc(Long caseId);
    long countByCaseId(Long caseId);
}
