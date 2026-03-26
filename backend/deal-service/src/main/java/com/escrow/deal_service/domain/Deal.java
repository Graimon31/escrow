package com.escrow.deal_service.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "deals")
public class Deal {
  @Id
  private UUID id;
  private String depositor;
  private String beneficiary;
  private BigDecimal amount;
  private String currency;
  private String subject;
  @Enumerated(EnumType.STRING)
  private DealState state;
  private Instant createdAt;

  @PrePersist
  void init() {
    if (id == null) id = UUID.randomUUID();
    if (createdAt == null) createdAt = Instant.now();
  }

  public UUID getId() { return id; }
  public String getDepositor() { return depositor; }
  public void setDepositor(String depositor) { this.depositor = depositor; }
  public String getBeneficiary() { return beneficiary; }
  public void setBeneficiary(String beneficiary) { this.beneficiary = beneficiary; }
  public BigDecimal getAmount() { return amount; }
  public void setAmount(BigDecimal amount) { this.amount = amount; }
  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }
  public String getSubject() { return subject; }
  public void setSubject(String subject) { this.subject = subject; }
  public DealState getState() { return state; }
  public void setState(DealState state) { this.state = state; }
  public Instant getCreatedAt() { return createdAt; }
}
