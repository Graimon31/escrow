package com.escrow.review_service.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "review_actions")
public class ReviewAction {
    @Id
    private UUID id;

    @Column(name = "deal_id", nullable = false)
    private UUID dealId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ReviewActionType actionType;

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
    public ReviewActionType getActionType() { return actionType; }
    public void setActionType(ReviewActionType actionType) { this.actionType = actionType; }
    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
