package com.escrow.payment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "escrow_accounts")
@Getter
@Setter
@NoArgsConstructor
public class EscrowAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "deal_id", nullable = false, unique = true)
    private UUID dealId;

    @Column(name = "depositor_id", nullable = false)
    private UUID depositorId;

    @Column(name = "beneficiary_id", nullable = false)
    private UUID beneficiaryId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency = "RUB";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EscrowAccountStatus status = EscrowAccountStatus.NOT_CREATED;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "funded_at")
    private LocalDateTime fundedAt;

    @Column(name = "released_at")
    private LocalDateTime releasedAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;
}
