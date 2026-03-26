package com.escrow.deal_service.service;

import com.escrow.deal_service.api.DealDtos.*;
import com.escrow.deal_service.domain.Deal;
import com.escrow.deal_service.domain.DealState;
import com.escrow.deal_service.repo.DealRepository;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DealService {
  private static final Logger log = LoggerFactory.getLogger(DealService.class);
  private final DealRepository repo;
  private final KafkaTemplate<String, String> kafka;

  private static final Map<DealState, Set<DealState>> ALLOWED = Map.ofEntries(
      Map.entry(DealState.DRAFT, Set.of(DealState.AWAITING_AGREEMENT)),
      Map.entry(DealState.AWAITING_AGREEMENT, Set.of(DealState.AGREED)),
      Map.entry(DealState.AGREED, Set.of(DealState.ACCOUNT_OPENED)),
      Map.entry(DealState.ACCOUNT_OPENED, Set.of(DealState.AWAITING_FUNDING)),
      Map.entry(DealState.AWAITING_FUNDING, Set.of(DealState.FUNDING_PROCESSING)),
      Map.entry(DealState.FUNDING_PROCESSING, Set.of(DealState.FUNDS_SECURED)),
      Map.entry(DealState.FUNDS_SECURED, Set.of(DealState.AWAITING_BENEFICIARY_FULFILLMENT)),
      Map.entry(DealState.AWAITING_BENEFICIARY_FULFILLMENT, Set.of(DealState.AWAITING_DEPOSITOR_REVIEW)),
      Map.entry(DealState.AWAITING_DEPOSITOR_REVIEW, Set.of(DealState.RELEASE_PENDING, DealState.REFUND_PENDING, DealState.DISPUTED)),
      Map.entry(DealState.RELEASE_PENDING, Set.of(DealState.RELEASED)),
      Map.entry(DealState.REFUND_PENDING, Set.of(DealState.REFUNDED)),
      Map.entry(DealState.DISPUTED, Set.of(DealState.RELEASE_PENDING, DealState.REFUND_PENDING)),
      Map.entry(DealState.RELEASED, Set.of(DealState.CLOSED)),
      Map.entry(DealState.REFUNDED, Set.of(DealState.CLOSED))
  );

  public DealService(DealRepository repo, KafkaTemplate<String, String> kafka) {
    this.repo = repo;
    this.kafka = kafka;
  }

  public DealResponse create(CreateDealRequest req) {
    Deal d = new Deal();
    d.setDepositor(req.depositor());
    d.setBeneficiary(req.beneficiary());
    d.setAmount(req.amount());
    d.setCurrency(req.currency());
    d.setSubject(req.subject());
    d.setState(DealState.DRAFT);
    d = repo.save(d);
    publishEvent(d.getId() + ":DRAFT");
    return toResponse(d);
  }

  public List<DealResponse> all() {
    return repo.findAll().stream().map(this::toResponse).toList();
  }

  public DealResponse transition(UUID id, DealState next) {
    Deal d = repo.findById(id).orElseThrow();
    if (!ALLOWED.getOrDefault(d.getState(), Set.of()).contains(next)) {
      throw new IllegalStateException("Forbidden transition " + d.getState() + " -> " + next);
    }
    d.setState(next);
    d = repo.save(d);
    publishEvent(d.getId() + ":" + next);
    return toResponse(d);
  }

  private void publishEvent(String payload) {
    try {
      kafka.send("deal-events", payload);
    } catch (RuntimeException ex) {
      log.warn("Kafka publish skipped: {}", ex.getMessage());
    }
  }

  private DealResponse toResponse(Deal d) {
    return new DealResponse(d.getId(), d.getDepositor(), d.getBeneficiary(), d.getAmount(), d.getCurrency(), d.getSubject(), d.getState());
  }
}
