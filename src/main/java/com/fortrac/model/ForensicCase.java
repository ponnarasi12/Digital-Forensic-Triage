package com.fortrac.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cases")
public class ForensicCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_number", nullable = false, unique = true)
    private String caseNumber;

    @Column(nullable = false)
    private String title;

    @Column(name = "investigator_name")
    private String investigatorName;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String status; // OPEN, IN_PROGRESS, CLOSED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public ForensicCase() {
        this.createdAt = LocalDateTime.now();
        this.status = "OPEN";
    }

    public ForensicCase(String caseNumber, String title, String investigatorName, String description) {
        this();
        this.caseNumber = caseNumber;
        this.title = title;
        this.investigatorName = investigatorName;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getInvestigatorName() { return investigatorName; }
    public void setInvestigatorName(String investigatorName) { this.investigatorName = investigatorName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
