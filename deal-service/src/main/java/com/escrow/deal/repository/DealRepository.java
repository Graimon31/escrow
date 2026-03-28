package com.escrow.deal.repository;

import com.escrow.deal.entity.Deal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DealRepository extends JpaRepository<Deal, UUID> {

    List<Deal> findByDepositorIdOrderByCreatedAtDesc(UUID depositorId);

    List<Deal> findByBeneficiaryIdOrderByCreatedAtDesc(UUID beneficiaryId);

    List<Deal> findByDepositorIdOrBeneficiaryIdOrderByCreatedAtDesc(UUID depositorId, UUID beneficiaryId);
}
