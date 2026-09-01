package com.fortrac.service;

import com.fortrac.model.Artifact;
import com.fortrac.model.EvidenceLink;
import com.fortrac.repository.EvidenceLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class CorrelationService {

    @Autowired
    private EvidenceLinkRepository evidenceLinkRepository;

    public void correlateEvidenceGraph(Long caseId, List<Artifact> artifacts) {
        for (int i = 0; i < artifacts.size(); i++) {
            Artifact a1 = artifacts.get(i);
            for (int j = i + 1; j < artifacts.size(); j++) {
                Artifact a2 = artifacts.get(j);

                // Check 1: Time correlation (within 10 minutes)
                if (a1.getEventTimestamp() != null && a2.getEventTimestamp() != null) {
                    long minutes = Math.abs(Duration.between(a1.getEventTimestamp(), a2.getEventTimestamp()).toMinutes());
                    if (minutes <= 10) {
                        double weight = Math.max(0.3, 1.0 - (minutes / 10.0));
                        evidenceLinkRepository.save(new EvidenceLink(caseId, a1.getId(), a2.getId(), "TIME_CORRELATED", weight));
                    }
                }

                // Check 2: Extraction relationship (zip -> exe/dll)
                if ("DOWNLOAD".equalsIgnoreCase(a1.getArtifactType()) && "FILE_EXTRACTION".equalsIgnoreCase(a2.getArtifactType())) {
                    evidenceLinkRepository.save(new EvidenceLink(caseId, a1.getId(), a2.getId(), "EXTRACTED_FROM", 0.95));
                }

                // Check 3: Execution relationship (extraction -> process)
                if ("FILE_EXTRACTION".equalsIgnoreCase(a1.getArtifactType()) && "PROCESS_EXEC".equalsIgnoreCase(a2.getArtifactType())) {
                    evidenceLinkRepository.save(new EvidenceLink(caseId, a1.getId(), a2.getId(), "LOADED_BY", 0.90));
                }

                // Check 4: Network connection relationship (process -> network)
                if ("PROCESS_EXEC".equalsIgnoreCase(a1.getArtifactType()) && "NETWORK_CONN".equalsIgnoreCase(a2.getArtifactType())) {
                    evidenceLinkRepository.save(new EvidenceLink(caseId, a1.getId(), a2.getId(), "CONNECTED_TO", 0.88));
                }
            }
        }
    }
}
