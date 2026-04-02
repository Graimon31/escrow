package com.escrow.payment.repository;

import com.escrow.payment.entity.EscrowAccount;
import com.escrow.payment.entity.EscrowAccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EscrowAccountRepository extends JpaRepository<EscrowAccount, UUID> {

    Optional<EscrowAccount> findByDealId(UUID dealId);

    List<EscrowAccount> findByStatus(EscrowAccountStatus status);

    List<EscrowAccount> findByDepositorIdOrBeneficiaryIdOrderByCreatedAtDesc(UUID depositorId, UUID beneficiaryId);
}
