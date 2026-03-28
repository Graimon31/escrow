package com.escrow.dispute_service.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DisputeCaseRepository extends JpaRepository<DisputeCase, UUID> {
    List<DisputeCase> findByDealIdOrderByCreatedAtDesc(UUID dealId);
    Optional<DisputeCase> findFirstByDealIdAndStatusOrderByCreatedAtDesc(UUID dealId, DisputeStatus status);
}
