package com.escrow.fulfillment_service.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FulfillmentRepository extends JpaRepository<FulfillmentRecord, UUID> {
    Optional<FulfillmentRecord> findByDealId(UUID dealId);
}
