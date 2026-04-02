package com.escrow.payment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ledger_accounts")
@Getter
@Setter
@NoArgsConstructor
public class LedgerAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "owner_id")
    private UUID ownerId;

    @Column(name = "owner_type", nullable = false, length = 20)
    private String ownerType = "USER";

    @Column(name = "account_type", nullable = false, length = 30)
    private String accountType;

    @Column(nullable = false, length = 3)
    private String currency = "RUB";

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
