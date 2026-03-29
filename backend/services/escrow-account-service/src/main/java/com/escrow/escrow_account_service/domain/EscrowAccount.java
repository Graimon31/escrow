package com.escrow.escrow_account_service.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "escrow_accounts")
public class EscrowAccount {

    @Id
    private UUID id;

    @Column(name = "deal_id", nullable = false, unique = true)
    private UUID dealId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EscrowAccountState state;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getDealId() { return dealId; }
    public void setDealId(UUID dealId) { this.dealId = dealId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public EscrowAccountState getState() { return state; }
    public void setState(EscrowAccountState state) { this.state = state; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
