package com.escrow.fulfillment_service.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FulfillmentEvent(UUID dealId, String eventType, String actor, OffsetDateTime occurredAt, String details) {
}
