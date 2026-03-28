package com.escrow.deal_service.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "deals")
public class Deal {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(name = "depositor_username", nullable = false)
    private String depositorUsername;

    @Column(name = "beneficiary_username", nullable = false)
    private String beneficiaryUsername;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DealState state;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getDepositorUsername() { return depositorUsername; }
    public void setDepositorUsername(String depositorUsername) { this.depositorUsername = depositorUsername; }
    public String getBeneficiaryUsername() { return beneficiaryUsername; }
    public void setBeneficiaryUsername(String beneficiaryUsername) { this.beneficiaryUsername = beneficiaryUsername; }
    public DealState getState() { return state; }
    public void setState(DealState state) { this.state = state; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
