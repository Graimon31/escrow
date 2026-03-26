package com.escrow.deal_service.repo;

import com.escrow.deal_service.domain.Deal;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DealRepository extends JpaRepository<Deal, UUID> {}
