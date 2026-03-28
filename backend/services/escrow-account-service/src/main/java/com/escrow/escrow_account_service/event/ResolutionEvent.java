package com.escrow.escrow_account_service.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ResolutionEvent(UUID dealId, String eventType, String actor, String comment, OffsetDateTime occurredAt) {
}
