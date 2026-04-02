package com.escrow.payment.repository;

import com.escrow.payment.entity.LedgerAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LedgerAccountRepository extends JpaRepository<LedgerAccount, UUID> {

    Optional<LedgerAccount> findByOwnerIdAndAccountTypeAndCurrency(UUID ownerId, String accountType, String currency);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT la FROM LedgerAccount la WHERE la.ownerId = :ownerId AND la.accountType = :accountType AND la.currency = :currency")
    Optional<LedgerAccount> findForUpdate(UUID ownerId, String accountType, String currency);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT la FROM LedgerAccount la WHERE la.id = :id")
    Optional<LedgerAccount> findByIdForUpdate(UUID id);

    List<LedgerAccount> findByOwnerId(UUID ownerId);
}
