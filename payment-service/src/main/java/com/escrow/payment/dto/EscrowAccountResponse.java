package com.escrow.payment.dto;

import com.escrow.payment.entity.EscrowAccount;
import com.escrow.payment.entity.EscrowAccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class EscrowAccountResponse {
    private UUID id;
    private UUID dealId;
    private UUID depositorId;
    private UUID beneficiaryId;
    private BigDecimal amount;
    private String currency;
    private EscrowAccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime fundedAt;
    private LocalDateTime releasedAt;
    private LocalDateTime refundedAt;

    public static EscrowAccountResponse from(EscrowAccount ea) {
        return new EscrowAccountResponse(
                ea.getId(), ea.getDealId(), ea.getDepositorId(), ea.getBeneficiaryId(),
                ea.getAmount(), ea.getCurrency(), ea.getStatus(),
                ea.getCreatedAt(), ea.getFundedAt(), ea.getReleasedAt(), ea.getRefundedAt());
    }
}
