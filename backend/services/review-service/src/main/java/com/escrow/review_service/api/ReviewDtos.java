package com.escrow.review_service.api;

import com.escrow.review_service.domain.ReviewAction;
import com.escrow.review_service.domain.ReviewActionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ReviewDtos {

    public record ReviewActionRequest(@NotNull UUID dealId, @NotNull ReviewActionType action, @NotBlank String comment) {}

    public record ReviewActionResponse(UUID id, UUID dealId, ReviewActionType action, String actor, String comment, OffsetDateTime createdAt) {
        public static ReviewActionResponse from(ReviewAction action) {
            return new ReviewActionResponse(action.getId(), action.getDealId(), action.getActionType(), action.getActor(), action.getComment(), action.getCreatedAt());
        }
    }
}
