package com.escrow.funding_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.escrow.funding_service.domain.*;
import com.escrow.funding_service.event.FundingEvent;
import com.escrow.funding_service.event.FundingEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class FundingService {

    private final FundingOperationRepository repository;
    private final AuditEventRepository auditEventRepository;
    private final FundingEventPublisher publisher;
    private final ObjectMapper objectMapper;

    public FundingService(FundingOperationRepository repository,
                          AuditEventRepository auditEventRepository,
                          FundingEventPublisher publisher,
                          ObjectMapper objectMapper) {
        this.repository = repository;
        this.auditEventRepository = auditEventRepository;
        this.publisher = publisher;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public FundingOperation deposit(UUID dealId, BigDecimal amount, String currency, String requestedBy, String idempotencyKey) {
        var existing = repository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            return existing.get();
        }

        FundingOperation op = new FundingOperation();
        op.setDealId(dealId);
        op.setAmount(amount);
        op.setCurrency(currency);
        op.setRequestedBy(requestedBy);
        op.setIdempotencyKey(idempotencyKey);
        op.setStatus(FundingStatus.REQUESTED);
        op = repository.save(op);

        op.setStatus(FundingStatus.FUNDING_PROCESSING);
        op = repository.save(op);
        publishAndAudit(op, "FUNDING_PROCESSING");

        op.setStatus(FundingStatus.FUNDS_SECURED);
        op = repository.save(op);
        publishAndAudit(op, "FUNDS_SECURED");

        return op;
    }

    private void publishAndAudit(FundingOperation operation, String eventType) {
        FundingEvent event = new FundingEvent(
                operation.getDealId(),
                operation.getAmount(),
                operation.getCurrency(),
                eventType,
                OffsetDateTime.now(),
                "funding-service"
        );
        publisher.publish(event);

        AuditEvent auditEvent = new AuditEvent();
        auditEvent.setOperationId(operation.getId());
        auditEvent.setDealId(operation.getDealId());
        auditEvent.setEventType(eventType);
        try {
            auditEvent.setEventPayload(objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            auditEvent.setEventPayload("{\"error\":\"serialization_failed\"}");
        }
        auditEventRepository.save(auditEvent);
    }

    public java.util.List<AuditEvent> auditTrail(UUID dealId) {
        return auditEventRepository.findTop50ByDealIdOrderByCreatedAtDesc(dealId);
    }
}
