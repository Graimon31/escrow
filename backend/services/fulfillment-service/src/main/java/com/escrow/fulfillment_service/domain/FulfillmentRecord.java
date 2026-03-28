package com.escrow.fulfillment_service.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "fulfillment_records")
public class FulfillmentRecord {
    @Id
    private UUID id;

    @Column(name = "deal_id", nullable = false, unique = true)
    private UUID dealId;

    @Column(name = "beneficiary_username", nullable = false)
    private String beneficiaryUsername;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String status;

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
    public String getBeneficiaryUsername() { return beneficiaryUsername; }
    public void setBeneficiaryUsername(String beneficiaryUsername) { this.beneficiaryUsername = beneficiaryUsername; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
