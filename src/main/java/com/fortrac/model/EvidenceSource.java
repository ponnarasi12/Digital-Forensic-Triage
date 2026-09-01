package com.fortrac.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evidence_sources")
public class EvidenceSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "source_name", nullable = false)
    private String sourceName;

    @Column(name = "source_type", nullable = false)
    private String sourceType; // EVTX, PREFETCH, BROWSER, USB_LOG, NETWORK, FILE_META

    @Column(name = "file_hash")
    private String fileHash;

    @Column(name = "artifact_count")
    private Integer artifactCount;

    @Column(name = "ingested_at")
    private LocalDateTime ingestedAt;

    public EvidenceSource() {
        this.ingestedAt = LocalDateTime.now();
        this.artifactCount = 0;
    }

    public EvidenceSource(Long caseId, String sourceName, String sourceType, String fileHash) {
        this();
        this.caseId = caseId;
        this.sourceName = sourceName;
        this.sourceType = sourceType;
        this.fileHash = fileHash;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public String getFileHash() { return fileHash; }
    public void setFileHash(String fileHash) { this.fileHash = fileHash; }

    public Integer getArtifactCount() { return artifactCount; }
    public void setArtifactCount(Integer artifactCount) { this.artifactCount = artifactCount; }

    public LocalDateTime getIngestedAt() { return ingestedAt; }
    public void setIngestedAt(LocalDateTime ingestedAt) { this.ingestedAt = ingestedAt; }
}
