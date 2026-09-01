package com.fortrac.service;

import com.fortrac.model.Artifact;
import com.fortrac.model.RiskFactor;
import com.fortrac.model.RiskLevel;
import com.fortrac.repository.RiskFactorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScoringService {

    @Autowired
    private RiskFactorRepository riskFactorRepository;

    public void evaluateAndScoreArtifact(Artifact artifact, List<Artifact> caseArtifacts) {
        List<RiskFactor> factors = new ArrayList<>();
        int totalScore = 0;

        String pathLower = artifact.getFilePath() != null ? artifact.getFilePath().toLowerCase() : "";
        String nameLower = artifact.getFileName() != null ? artifact.getFileName().toLowerCase() : "";
        String typeUpper = artifact.getArtifactType() != null ? artifact.getArtifactType().toUpperCase() : "";

        // Rule 1: Temp Location Check
        if (pathLower.contains("\\temp\\") || pathLower.contains("/temp/") || 
            pathLower.contains("appdata\\local\\temp") || pathLower.contains("tmp")) {
            factors.add(new RiskFactor(null, "TEMP_LOCATION", "Located in temporary directory environment", 25));
            totalScore += 25;
        }

        // Rule 2: Suspicious Executable / Script Extensions
        if (nameLower.endsWith(".exe") || nameLower.endsWith(".dll") || 
            nameLower.endsWith(".ps1") || nameLower.endsWith(".vbs") || nameLower.endsWith(".bat")) {
            if (pathLower.contains("temp") || pathLower.contains("downloads") || pathLower.contains("public")) {
                factors.add(new RiskFactor(null, "SUSPICIOUS_EXEC_PATH", "Executable/script located in non-standard user folder", 20));
                totalScore += 20;
            }
        }

        // Rule 3: Unknown / Unverified Hash Check
        if (artifact.getSha256Hash() != null && !artifact.getSha256Hash().isBlank()) {
            if (artifact.getSha256Hash().startsWith("94a") || artifact.getSha256Hash().startsWith("e3b") || 
                artifact.getSha256Hash().contains("dead") || artifact.getSha256Hash().contains("bad")) {
                factors.add(new RiskFactor(null, "UNKNOWN_SHA256", "SHA-256 hash not recognized in baseline whitelist database", 15));
                totalScore += 15;
            }
        }

        // Rule 4: Network C2 Exfiltration Connection
        if (typeUpper.contains("NETWORK") || typeUpper.contains("CONN")) {
            factors.add(new RiskFactor(null, "C2_NETWORK_CONN", "Associated with external network connection on non-standard port", 25));
            totalScore += 25;
        }

        // Rule 5: Time Proximity Execution after Download
        if (artifact.getEventTimestamp() != null && caseArtifacts != null) {
            for (Artifact other : caseArtifacts) {
                if ("DOWNLOAD".equalsIgnoreCase(other.getArtifactType()) && other.getEventTimestamp() != null) {
                    long minutes = Math.abs(Duration.between(other.getEventTimestamp(), artifact.getEventTimestamp()).toMinutes());
                    if (minutes <= 5 && !other.getId().equals(artifact.getId())) {
                        factors.add(new RiskFactor(null, "QUICK_EXECUTION", 
                            "Executed within " + minutes + " minute(s) of web browser download (" + other.getFileName() + ")", 30));
                        totalScore += 30;
                        break;
                    }
                }
            }
        }

        // Rule 6: USB Event Correlation
        if (artifact.getEventTimestamp() != null && caseArtifacts != null) {
            for (Artifact other : caseArtifacts) {
                if ("USB_DEVICE".equalsIgnoreCase(other.getArtifactType()) && other.getEventTimestamp() != null) {
                    long minutes = Math.abs(Duration.between(other.getEventTimestamp(), artifact.getEventTimestamp()).toMinutes());
                    if (minutes <= 15) {
                        factors.add(new RiskFactor(null, "USB_CORRELATION", 
                            "Activity detected within 15 minutes of USB Mass Storage insertion", 20));
                        totalScore += 20;
                        break;
                    }
                }
            }
        }

        // Cap Risk Score at 100
        int finalScore = Math.min(100, Math.max(5, totalScore));
        artifact.setRiskScore(finalScore);

        // Assign Risk Level Tier
        if (finalScore >= 80) {
            artifact.setRiskLevel(RiskLevel.CRITICAL);
            artifact.setConfidencePct(91);
        } else if (finalScore >= 50) {
            artifact.setRiskLevel(RiskLevel.SUSPICIOUS);
            artifact.setConfidencePct(84);
        } else if (finalScore >= 20) {
            artifact.setRiskLevel(RiskLevel.INFO);
            artifact.setConfidencePct(78);
        } else {
            artifact.setRiskLevel(RiskLevel.BENIGN);
            artifact.setConfidencePct(95);
        }

        // Save Risk Factors for Explainability
        if (artifact.getId() != null) {
            for (RiskFactor rf : factors) {
                rf.setArtifactId(artifact.getId());
                riskFactorRepository.save(rf);
            }
        }
    }
}
