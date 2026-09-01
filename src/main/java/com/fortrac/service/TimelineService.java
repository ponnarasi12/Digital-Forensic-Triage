package com.fortrac.service;

import com.fortrac.model.Artifact;
import com.fortrac.model.TimelineEvent;
import com.fortrac.repository.TimelineEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimelineService {

    @Autowired
    private TimelineEventRepository timelineEventRepository;

    public void reconstructTimeline(Long caseId, List<Artifact> artifacts) {
        for (Artifact artifact : artifacts) {
            if (artifact.getEventTimestamp() == null) continue;

            String category = determineCategory(artifact);
            String severity = artifact.getRiskLevel() != null ? artifact.getRiskLevel().name() : "INFO";
            String summary = buildSummary(artifact);

            TimelineEvent event = new TimelineEvent(
                caseId,
                artifact.getId(),
                artifact.getEventTimestamp(),
                category,
                summary,
                severity
            );

            timelineEventRepository.save(event);
        }
    }

    private String determineCategory(Artifact artifact) {
        String type = artifact.getArtifactType() != null ? artifact.getArtifactType().toUpperCase() : "";
        if (type.contains("USB")) return "USB Connected";
        if (type.contains("DOWNLOAD")) return "File Downloaded";
        if (type.contains("EXTRACTION") || type.contains("ZIP")) return "Archive Extracted";
        if (type.contains("EXEC") || type.contains("PROCESS")) return "Suspicious Process";
        if (type.contains("NETWORK") || type.contains("CONN")) return "External Connection";
        return "System Event";
    }

    private String buildSummary(Artifact artifact) {
        String type = determineCategory(artifact);
        String file = artifact.getFileName() != null ? artifact.getFileName() : "Unknown Artifact";
        return type + " — " + file + " (" + artifact.getFilePath() + ")";
    }
}
