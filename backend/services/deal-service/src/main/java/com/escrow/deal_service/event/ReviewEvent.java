package com.escrow.deal_service.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ReviewEvent(UUID dealId, String eventType, String actor, String comment, OffsetDateTime occurredAt) {
}
