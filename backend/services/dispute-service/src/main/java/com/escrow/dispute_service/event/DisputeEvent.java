package com.escrow.dispute_service.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DisputeEvent(UUID dealId, String eventType, String actor, String comment, OffsetDateTime occurredAt) {
}
