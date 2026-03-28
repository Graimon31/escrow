package com.escrow.funding_service.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FundingOperationRepository extends JpaRepository<FundingOperation, UUID> {
    Optional<FundingOperation> findByIdempotencyKey(String idempotencyKey);
}
