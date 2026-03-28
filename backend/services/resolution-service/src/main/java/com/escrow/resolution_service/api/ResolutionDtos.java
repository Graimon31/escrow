package com.escrow.resolution_service.api;

import com.escrow.resolution_service.domain.ResolutionDecision;
import com.escrow.resolution_service.domain.ResolutionOutcome;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ResolutionDtos {
    public record ResolutionRequest(@NotNull UUID dealId, @NotNull ResolutionOutcome outcome, @NotBlank String comment) {}

    public record ResolutionResponse(UUID id, UUID dealId, ResolutionOutcome outcome, String actor, String comment, OffsetDateTime createdAt) {
        public static ResolutionResponse from(ResolutionDecision decision) {
            return new ResolutionResponse(decision.getId(), decision.getDealId(), decision.getOutcome(), decision.getActor(), decision.getComment(), decision.getCreatedAt());
        }
    }
}
