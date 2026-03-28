package com.escrow.resolution_service.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ResolutionDecisionRepository extends JpaRepository<ResolutionDecision, UUID> {
    List<ResolutionDecision> findByDealIdOrderByCreatedAtDesc(UUID dealId);
}
