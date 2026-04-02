package com.escrow.payment.repository;

import com.escrow.payment.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByDealIdOrderByCreatedAtDesc(UUID dealId);

    List<Transaction> findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(UUID fromId, UUID toId);
}
