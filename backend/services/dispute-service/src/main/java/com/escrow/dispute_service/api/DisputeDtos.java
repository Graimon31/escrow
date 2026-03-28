package com.escrow.dispute_service.api;

import com.escrow.dispute_service.domain.DisputeCase;
import com.escrow.dispute_service.domain.DisputeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public class DisputeDtos {
    public record OpenDisputeRequest(@NotNull UUID dealId, @NotBlank String reason) {}

    public record DisputeCaseResponse(UUID id, UUID dealId, DisputeStatus status, String openedBy, String reason,
                                      OffsetDateTime createdAt, OffsetDateTime resolvedAt, String resolvedBy, String resolutionComment) {
        public static DisputeCaseResponse from(DisputeCase disputeCase) {
            return new DisputeCaseResponse(
                    disputeCase.getId(),
                    disputeCase.getDealId(),
                    disputeCase.getStatus(),
                    disputeCase.getOpenedBy(),
                    disputeCase.getReason(),
                    disputeCase.getCreatedAt(),
                    disputeCase.getResolvedAt(),
                    disputeCase.getResolvedBy(),
                    disputeCase.getResolutionComment()
            );
        }
    }
}
