package com.escrow.funding_service.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "funding_audit_events")
public class AuditEvent {

    @Id
    private UUID id;

    @Column(name = "operation_id", nullable = false)
    private UUID operationId;

    @Column(name = "deal_id", nullable = false)
    private UUID dealId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "event_payload", nullable = false, columnDefinition = "TEXT")
    private String eventPayload;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void onPersist() {
        if (id == null) id = UUID.randomUUID();
        createdAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getOperationId() { return operationId; }
    public UUID getDealId() { return dealId; }
    public String getEventType() { return eventType; }
    public String getEventPayload() { return eventPayload; }
    public OffsetDateTime getCreatedAt() { return createdAt; }

    public void setOperationId(UUID operationId) { this.operationId = operationId; }
    public void setDealId(UUID dealId) { this.dealId = dealId; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public void setEventPayload(String eventPayload) { this.eventPayload = eventPayload; }
}
