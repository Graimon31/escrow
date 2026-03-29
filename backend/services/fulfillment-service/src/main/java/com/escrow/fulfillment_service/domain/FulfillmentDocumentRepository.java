package com.escrow.fulfillment_service.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FulfillmentDocumentRepository extends JpaRepository<FulfillmentDocumentMetadata, UUID> {
    List<FulfillmentDocumentMetadata> findByDealId(UUID dealId);
}
