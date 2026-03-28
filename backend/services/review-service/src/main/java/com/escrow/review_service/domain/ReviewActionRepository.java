package com.escrow.review_service.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewActionRepository extends JpaRepository<ReviewAction, UUID> {
    List<ReviewAction> findByDealIdOrderByCreatedAtAsc(UUID dealId);
}
