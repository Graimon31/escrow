package com.escrow.resolution_service.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "resolution_decisions")
public class ResolutionDecision {
    @Id
    private UUID id;

    @Column(name = "deal_id", nullable = false)
    private UUID dealId;

    @Enumerated(EnumType.STRING)
    @Column(name = "outcome", nullable = false)
    private ResolutionOutcome outcome;

    @Column(nullable = false)
    private String actor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void init() {
        if (id == null) id = UUID.randomUUID();
        createdAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getDealId() { return dealId; }
    public void setDealId(UUID dealId) { this.dealId = dealId; }
    public ResolutionOutcome getOutcome() { return outcome; }
    public void setOutcome(ResolutionOutcome outcome) { this.outcome = outcome; }
    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
