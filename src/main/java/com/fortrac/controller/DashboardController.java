package com.fortrac.controller;

import com.fortrac.model.AiSummary;
import com.fortrac.model.Artifact;
import com.fortrac.model.ForensicCase;
import com.fortrac.model.TimelineEvent;
import com.fortrac.repository.AiSummaryRepository;
import com.fortrac.repository.ArtifactRepository;
import com.fortrac.repository.CaseRepository;
import com.fortrac.repository.TimelineEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class DashboardController {

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private TimelineEventRepository timelineEventRepository;

    @Autowired
    private AiSummaryRepository aiSummaryRepository;

    @GetMapping({"/", "/dashboard"})
    public String renderDashboard(Model model) {
        ForensicCase fCase = caseRepository.findAll().stream().findFirst()
                .orElse(new ForensicCase("1042", "Ransomware Incident", "Lead Analyst", "Default Case"));

        Long caseId = fCase.getId();

        model.addAttribute("case", fCase);
        model.addAttribute("totalEvidence", artifactRepository.countByCaseId(caseId));
        model.addAttribute("criticalCount", artifactRepository.countByCaseIdAndRiskLevel(caseId, com.fortrac.model.RiskLevel.CRITICAL));
        model.addAttribute("suspiciousCount", artifactRepository.countByCaseIdAndRiskLevel(caseId, com.fortrac.model.RiskLevel.SUSPICIOUS));
        model.addAttribute("timelineCount", timelineEventRepository.countByCaseId(caseId));

        List<Artifact> topPriority = artifactRepository.findByCaseIdOrderByRiskScoreDesc(caseId).stream().limit(5).toList();
        model.addAttribute("topPriorityEvidence", topPriority);

        List<TimelineEvent> timelineEvents = timelineEventRepository.findByCaseIdOrderByEventTimeAsc(caseId).stream().limit(6).toList();
        model.addAttribute("timelineEvents", timelineEvents);

        Optional<AiSummary> aiOpt = aiSummaryRepository.findByCaseId(caseId);
        model.addAttribute("aiSummary", aiOpt.orElse(null));

        return "dashboard";
    }
}
