package com.escrow.audit.consumer;

import com.escrow.audit.entity.AuditLog;
import com.escrow.audit.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditEventConsumer {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "deal-events", groupId = "audit-service-deals")
    public void handleDealEvent(String message) {
        saveAuditLog("deal-service", "Deal", message);
    }

    @KafkaListener(topics = "escrow-escrowaccount-events", groupId = "audit-service-escrow")
    public void handleEscrowEvent(String message) {
        saveAuditLog("payment-service", "EscrowAccount", message);
    }

    private void saveAuditLog(String service, String aggregateType, String payload) {
        try {
            JsonNode json = objectMapper.readTree(payload);
            String eventType = json.has("type") ? json.get("type").asText() : json.has("eventType") ? json.get("eventType").asText() : "UNKNOWN";
            UUID aggregateId = json.has("dealId") ? UUID.fromString(json.get("dealId").asText()) : UUID.randomUUID();
            UUID actorId = json.has("actorId") ? UUID.fromString(json.get("actorId").asText()) : null;
            String actorRole = json.has("actorRole") ? json.get("actorRole").asText() : null;

            AuditLog entry = AuditLog.builder()
                    .service(service)
                    .eventType(eventType)
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .actorId(actorId)
                    .actorRole(actorRole)
                    .payload(payload)
                    .build();
            auditLogRepository.save(entry);
            log.debug("Audit log saved: {} {} {}", service, eventType, aggregateId);
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage());
        }
    }
}
