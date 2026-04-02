package com.escrow.audit.repository;

import com.escrow.audit.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByAggregateTypeAndAggregateIdOrderByTimestampDesc(String aggregateType, UUID aggregateId);

    List<AuditLog> findByActorIdOrderByTimestampDesc(UUID actorId);

    Page<AuditLog> findAllByOrderByTimestampDesc(Pageable pageable);
}
