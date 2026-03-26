package com.escrow.deal_service.api;

import com.escrow.deal_service.domain.DealState;
import java.math.BigDecimal;
import java.util.UUID;

public class DealDtos {
  public record CreateDealRequest(String depositor, String beneficiary, BigDecimal amount, String currency, String subject) {}
  public record TransitionRequest(DealState state) {}
  public record DealResponse(UUID id, String depositor, String beneficiary, BigDecimal amount, String currency, String subject, DealState state) {}
}
