package com.escrow.deal_service.api;

import com.escrow.deal_service.domain.Deal;
import com.escrow.deal_service.domain.DealState;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class DealDtos {

    public record CreateDealRequest(
            @NotBlank String title,
            @NotNull @DecimalMin("0.01") BigDecimal amount,
            @NotBlank String currency,
            @NotBlank String beneficiaryUsername) {
    }

    public record DealResponse(
            UUID id,
            String title,
            BigDecimal amount,
            String currency,
            String depositorUsername,
            String beneficiaryUsername,
            DealState state) {
        public static DealResponse from(Deal deal) {
            return new DealResponse(deal.getId(), deal.getTitle(), deal.getAmount(), deal.getCurrency(),
                    deal.getDepositorUsername(), deal.getBeneficiaryUsername(), deal.getState());
        }
    }
}
