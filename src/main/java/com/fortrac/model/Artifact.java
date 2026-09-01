package com.fortrac.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "artifacts")
public class Artifact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "artifact_type", nullable = false)
    private String artifactType; // USB_DEVICE, DOWNLOAD, FILE_EXTRACTION, PROCESS_EXEC, NETWORK_CONN

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path", length = 1000)
    private String filePath;

    @Column(name = "sha256_hash")
    private String sha256Hash;

    @Column(name = "event_timestamp")
    private LocalDateTime eventTimestamp;

    @Column(name = "risk_score")
    private Integer riskScore; // 0 to 100

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level")
    private RiskLevel riskLevel;

    @Column(name = "confidence_pct")
    private Integer confidencePct; // e.g. 91%

    @Column(length = 2000)
    private String metadataJson;

    public Artifact() {
        this.riskScore = 0;
        this.riskLevel = RiskLevel.BENIGN;
        this.confidencePct = 85;
    }

    public Artifact(Long caseId, Long sourceId, String artifactType, String fileName, String filePath, 
                    String sha256Hash, LocalDateTime eventTimestamp) {
        this();
        this.caseId = caseId;
        this.sourceId = sourceId;
        this.artifactType = artifactType;
        this.fileName = fileName;
        this.filePath = filePath;
        this.sha256Hash = sha256Hash;
        this.eventTimestamp = eventTimestamp;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }

    public Long getSourceId() { return sourceId; }
    public void setSourceId(Long sourceId) { this.sourceId = sourceId; }

    public String getArtifactType() { return artifactType; }
    public void setArtifactType(String artifactType) { this.artifactType = artifactType; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getSha256Hash() { return sha256Hash; }
    public void setSha256Hash(String sha256Hash) { this.sha256Hash = sha256Hash; }

    public LocalDateTime getEventTimestamp() { return eventTimestamp; }
    public void setEventTimestamp(LocalDateTime eventTimestamp) { this.eventTimestamp = eventTimestamp; }

    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    public Integer getConfidencePct() { return confidencePct; }
    public void setConfidencePct(Integer confidencePct) { this.confidencePct = confidencePct; }

    public String getMetadataJson() { return metadataJson; }
    public void setMetadataJson(String metadataJson) { this.metadataJson = metadataJson; }
}
