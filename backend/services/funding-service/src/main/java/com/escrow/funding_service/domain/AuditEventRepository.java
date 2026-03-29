package com.escrow.funding_service.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {
    List<AuditEvent> findTop50ByDealIdOrderByCreatedAtDesc(UUID dealId);
}
