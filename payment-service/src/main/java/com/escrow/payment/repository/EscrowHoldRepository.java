package com.escrow.payment.repository;

import com.escrow.payment.entity.EscrowHold;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EscrowHoldRepository extends JpaRepository<EscrowHold, UUID> {

    Optional<EscrowHold> findByDealId(UUID dealId);
}
