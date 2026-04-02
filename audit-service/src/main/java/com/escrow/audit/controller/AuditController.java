package com.escrow.audit.controller;

import com.escrow.audit.entity.AuditLog;
import com.escrow.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public ResponseEntity<List<AuditLog>> getRecentLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(
                auditLogRepository.findAllByOrderByTimestampDesc(PageRequest.of(page, size)).getContent());
    }

    @GetMapping("/deal/{dealId}")
    public ResponseEntity<List<AuditLog>> getByDeal(@PathVariable UUID dealId) {
        return ResponseEntity.ok(
                auditLogRepository.findByAggregateTypeAndAggregateIdOrderByTimestampDesc("Deal", dealId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLog>> getByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(auditLogRepository.findByActorIdOrderByTimestampDesc(userId));
    }
}
