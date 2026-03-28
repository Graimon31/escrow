package com.escrow.escrow_account_service.event;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record FundingEvent(
        UUID dealId,
        BigDecimal amount,
        String currency,
        String eventType,
        OffsetDateTime occurredAt,
        String source
) {
}
