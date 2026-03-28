package com.escrow.deal.dto;

import com.escrow.deal.entity.Deal;
import com.escrow.deal.entity.DealStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class DealResponse {
    private UUID id;
    private String title;
    private String description;
    private BigDecimal amount;
    private String currency;
    private UUID depositorId;
    private UUID beneficiaryId;
    private DealStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DealResponse from(Deal deal) {
        return new DealResponse(
                deal.getId(),
                deal.getTitle(),
                deal.getDescription(),
                deal.getAmount(),
                deal.getCurrency(),
                deal.getDepositorId(),
                deal.getBeneficiaryId(),
                deal.getStatus(),
                deal.getCreatedAt(),
                deal.getUpdatedAt()
        );
    }
}
