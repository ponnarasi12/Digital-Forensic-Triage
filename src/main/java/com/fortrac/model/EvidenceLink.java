package com.fortrac.model;

import jakarta.persistence.*;

@Entity
@Table(name = "evidence_links")
public class EvidenceLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "source_artifact_id", nullable = false)
    private Long sourceArtifactId;

    @Column(name = "target_artifact_id", nullable = false)
    private Long targetArtifactId;

    @Column(name = "relationship_type", nullable = false)
    private String relationshipType; // EXTRACTED_FROM, LOADED_BY, CONNECTED_TO, TIME_CORRELATED, PRODUCED_BY

    @Column(nullable = false)
    private Double weight; // Link strength, 0.0 - 1.0

    public EvidenceLink() {}

    public EvidenceLink(Long caseId, Long sourceArtifactId, Long targetArtifactId, String relationshipType, Double weight) {
        this.caseId = caseId;
        this.sourceArtifactId = sourceArtifactId;
        this.targetArtifactId = targetArtifactId;
        this.relationshipType = relationshipType;
        this.weight = weight;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }

    public Long getSourceArtifactId() { return sourceArtifactId; }
    public void setSourceArtifactId(Long sourceArtifactId) { this.sourceArtifactId = sourceArtifactId; }

    public Long getTargetArtifactId() { return targetArtifactId; }
    public void setTargetArtifactId(Long targetArtifactId) { this.targetArtifactId = targetArtifactId; }

    public String getRelationshipType() { return relationshipType; }
    public void setRelationshipType(String relationshipType) { this.relationshipType = relationshipType; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
}
