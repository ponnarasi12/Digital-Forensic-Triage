package com.fortrac.model;

import jakarta.persistence.*;

@Entity
@Table(name = "risk_factors")
public class RiskFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "artifact_id", nullable = false)
    private Long artifactId;

    @Column(name = "factor_code", nullable = false)
    private String factorCode;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(name = "score_impact", nullable = false)
    private Integer scoreImpact;

    public RiskFactor() {}

    public RiskFactor(Long artifactId, String factorCode, String description, Integer scoreImpact) {
        this.artifactId = artifactId;
        this.factorCode = factorCode;
        this.description = description;
        this.scoreImpact = scoreImpact;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getArtifactId() { return artifactId; }
    public void setArtifactId(Long artifactId) { this.artifactId = artifactId; }

    public String getFactorCode() { return factorCode; }
    public void setFactorCode(String factorCode) { this.factorCode = factorCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getScoreImpact() { return scoreImpact; }
    public void setScoreImpact(Integer scoreImpact) { this.scoreImpact = scoreImpact; }
}
