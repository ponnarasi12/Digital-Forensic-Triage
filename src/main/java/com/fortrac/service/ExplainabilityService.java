package com.fortrac.service;

import com.fortrac.model.Artifact;
import com.fortrac.model.EvidenceLink;
import com.fortrac.model.RiskFactor;
import com.fortrac.repository.ArtifactRepository;
import com.fortrac.repository.EvidenceLinkRepository;
import com.fortrac.repository.RiskFactorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExplainabilityService {

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private RiskFactorRepository riskFactorRepository;

    @Autowired
    private EvidenceLinkRepository evidenceLinkRepository;

    public Map<String, Object> getExplainabilityPayload(Long artifactId) {
        Optional<Artifact> optArtifact = artifactRepository.findById(artifactId);
        if (optArtifact.isEmpty()) {
            return Map.of("error", "Artifact not found");
        }

        Artifact artifact = optArtifact.get();
        List<RiskFactor> riskFactors = riskFactorRepository.findByArtifactId(artifactId);
        List<EvidenceLink> links = evidenceLinkRepository.findBySourceArtifactIdOrTargetArtifactId(artifactId, artifactId);

        List<String> reasonList = new ArrayList<>();
        for (RiskFactor rf : riskFactors) {
            reasonList.add(rf.getDescription());
        }

        // Add Graph correlation justification reason
        if (!links.isEmpty()) {
            reasonList.add("Connected to " + links.size() + " other correlated forensic artifact(s) in case evidence graph.");
        }

        List<Map<String, Object>> correlatedArtifacts = new ArrayList<>();
        for (EvidenceLink link : links) {
            Long otherId = link.getSourceArtifactId().equals(artifactId) ? link.getTargetArtifactId() : link.getSourceArtifactId();
            Optional<Artifact> optOther = artifactRepository.findById(otherId);
            if (optOther.isPresent()) {
                Artifact other = optOther.get();
                Map<String, Object> item = new HashMap<>();
                item.put("id", other.getId());
                item.put("fileName", other.getFileName());
                item.put("artifactType", other.getArtifactType());
                item.put("relation", link.getRelationshipType());
                item.put("riskScore", other.getRiskScore());
                correlatedArtifacts.add(item);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("artifactId", artifact.getId());
        response.put("fileName", artifact.getFileName());
        response.put("filePath", artifact.getFilePath());
        response.put("sha256Hash", artifact.getSha256Hash());
        response.put("riskScore", artifact.getRiskScore());
        response.put("riskLevel", artifact.getRiskLevel().name());
        response.put("confidencePct", artifact.getConfidencePct() != null ? artifact.getConfidencePct() : 91);
        response.put("reasons", reasonList);
        response.put("correlatedArtifacts", correlatedArtifacts);

        return response;
    }
}
