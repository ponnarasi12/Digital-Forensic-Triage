package com.fortrac.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_summaries")
public class AiSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false, unique = true)
    private Long caseId;

    @Column(name = "hypothesis_title")
    private String hypothesisTitle;

    @Column(length = 4000)
    private String summaryText;

    @Column(length = 2000)
    private String attackVector;

    @Column(name = "confidence_score")
    private Integer confidenceScore; // e.g. 91%

    @Column(length = 2000)
    private String recommendedActions;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    public AiSummary() {
        this.generatedAt = LocalDateTime.now();
    }

    public AiSummary(Long caseId, String hypothesisTitle, String summaryText, String attackVector, 
                     Integer confidenceScore, String recommendedActions) {
        this();
        this.caseId = caseId;
        this.hypothesisTitle = hypothesisTitle;
        this.summaryText = summaryText;
        this.attackVector = attackVector;
        this.confidenceScore = confidenceScore;
        this.recommendedActions = recommendedActions;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }

    public String getHypothesisTitle() { return hypothesisTitle; }
    public void setHypothesisTitle(String hypothesisTitle) { this.hypothesisTitle = hypothesisTitle; }

    public String getSummaryText() { return summaryText; }
    public void setSummaryText(String summaryText) { this.summaryText = summaryText; }

    public String getAttackVector() { return attackVector; }
    public void setAttackVector(String attackVector) { this.attackVector = attackVector; }

    public Integer getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Integer confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getRecommendedActions() { return recommendedActions; }
    public void setRecommendedActions(String recommendedActions) { this.recommendedActions = recommendedActions; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
