package com.escrow.deal.repository;

import com.escrow.deal.entity.DealEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DealEventRepository extends JpaRepository<DealEvent, UUID> {

    List<DealEvent> findByDealIdOrderByCreatedAtAsc(UUID dealId);
}
