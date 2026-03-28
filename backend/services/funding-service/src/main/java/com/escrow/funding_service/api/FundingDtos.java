package com.escrow.funding_service.api;

import com.escrow.funding_service.domain.AuditEvent;
import com.escrow.funding_service.domain.FundingOperation;
import com.escrow.funding_service.domain.FundingStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class FundingDtos {

    public record DepositRequest(@NotNull UUID dealId, @NotNull @DecimalMin("0.01") BigDecimal amount, @NotBlank String currency) {}

    public record DepositResponse(UUID operationId, UUID dealId, FundingStatus status, BigDecimal amount, String currency) {
        public static DepositResponse from(FundingOperation op) {
            return new DepositResponse(op.getId(), op.getDealId(), op.getStatus(), op.getAmount(), op.getCurrency());
        }
    }

    public record AuditRecordResponse(UUID id, UUID operationId, UUID dealId, String eventType, String eventPayload, OffsetDateTime createdAt) {
        public static AuditRecordResponse from(AuditEvent event) {
            return new AuditRecordResponse(event.getId(), event.getOperationId(), event.getDealId(), event.getEventType(), event.getEventPayload(), event.getCreatedAt());
        }
    }
}
