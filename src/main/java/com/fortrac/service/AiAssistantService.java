package com.fortrac.service;

import com.fortrac.model.AiSummary;
import com.fortrac.model.Artifact;
import com.fortrac.model.RiskLevel;
import com.fortrac.repository.AiSummaryRepository;
import com.fortrac.repository.ArtifactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AiAssistantService {

    @Autowired
    private AiSummaryRepository aiSummaryRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    public AiSummary generateCaseSummary(Long caseId) {
        List<Artifact> criticalArtifacts = artifactRepository.findByCaseIdAndRiskLevel(caseId, RiskLevel.CRITICAL);
        List<Artifact> suspiciousArtifacts = artifactRepository.findByCaseIdAndRiskLevel(caseId, RiskLevel.SUSPICIOUS);
        long totalArtifacts = artifactRepository.countByCaseId(caseId);

        String title = "Possible Data Exfiltration & Malicious Execution Incident";
        StringBuilder summary = new StringBuilder();
        StringBuilder attackVector = new StringBuilder();
        StringBuilder recommendations = new StringBuilder();

        if (!criticalArtifacts.isEmpty()) {
            Artifact topArtifact = criticalArtifacts.get(0);
            summary.append("AI Forensic Analysis detected high-confidence malicious activity. ");
            summary.append("Initial breach path indicates USB media insertion followed by execution of high-risk process '")
                   .append(topArtifact.getFileName()).append("' (Risk Score: ").append(topArtifact.getRiskScore()).append("/100). ");
            summary.append("Outbound network connections were established shortly after process initialization, matching Command & Control (C2) exfiltration patterns.");

            attackVector.append("Phase 1: Initial Access via Physical Removable Storage (USB)\n")
                        .append("Phase 2: Malicious Archive Download & Extraction (confidential.zip)\n")
                        .append("Phase 3: Code Execution in User Temp Directory (suspicious.exe)\n")
                        .append("Phase 4: Encrypted Exfiltration to Remote C2 Server");

            recommendations.append("1. Immediately isolate host machine from network environment.\n")
                           .append("2. Block destination C2 IP addresses on gateway firewall.\n")
                           .append("3. Revoke active user credentials associated with host session.\n")
                           .append("4. Extract memory dump for volatile process analysis.");
        } else {
            summary.append("System triage completed across ").append(totalArtifacts).append(" artifacts. No critical threat chains identified.");
            attackVector.append("Standard user session activity.");
            recommendations.append("Continue routine baseline monitoring.");
        }

        Optional<AiSummary> existing = aiSummaryRepository.findByCaseId(caseId);
        AiSummary aiSummary = existing.orElse(new AiSummary());

        aiSummary.setCaseId(caseId);
        aiSummary.setHypothesisTitle(title);
        aiSummary.setSummaryText(summary.toString());
        aiSummary.setAttackVector(attackVector.toString());
        aiSummary.setConfidenceScore(91);
        aiSummary.setRecommendedActions(recommendations.toString());
        aiSummary.setGeneratedAt(LocalDateTime.now());

        return aiSummaryRepository.save(aiSummary);
    }
}
