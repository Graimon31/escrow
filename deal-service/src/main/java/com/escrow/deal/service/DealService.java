package com.escrow.deal.service;

import com.escrow.deal.dto.CreateDealRequest;
import com.escrow.deal.dto.DealEventResponse;
import com.escrow.deal.dto.DealResponse;
import com.escrow.deal.entity.Deal;
import com.escrow.deal.entity.DealEvent;
import com.escrow.deal.entity.DealStatus;
import com.escrow.deal.messaging.DealEventProducer;
import com.escrow.deal.repository.DealEventRepository;
import com.escrow.deal.repository.DealRepository;
import com.escrow.deal.statemachine.DealStateMachine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealService {

    private final DealRepository dealRepository;
    private final DealEventRepository dealEventRepository;
    private final DealEventProducer dealEventProducer;
    private final RestClient paymentServiceClient;

    @Transactional
    public DealResponse createDeal(CreateDealRequest request, UUID depositorId) {
        Deal deal = new Deal();
        deal.setTitle(request.getTitle());
        deal.setDescription(request.getDescription());
        deal.setAmount(request.getAmount());
        deal.setCurrency(request.getCurrency() != null ? request.getCurrency() : "RUB");
        deal.setDepositorId(depositorId);
        deal.setBeneficiaryId(request.getBeneficiaryId());
        deal.setStatus(DealStatus.CREATED);
        dealRepository.save(deal);

        recordEvent(deal, "DEAL_CREATED", depositorId, "DEPOSITOR", null, DealStatus.CREATED);
        dealEventProducer.publish("DEAL_CREATED", deal);

        return DealResponse.from(deal);
    }

    @Transactional
    public DealResponse fundDeal(UUID dealId, UUID userId, String userRole) {
        Deal deal = findDealOrThrow(dealId);
        if (!deal.getDepositorId().equals(userId)) {
            throw new SecurityException("Only depositor can fund the deal");
        }
        DealStateMachine.validate(deal.getStatus(), DealStatus.FUNDED);

        // Call payment-service to hold funds
        paymentServiceClient.post()
                .uri("/internal/payments/hold")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "dealId", deal.getId(),
                        "depositorId", deal.getDepositorId(),
                        "amount", deal.getAmount()
                ))
                .retrieve()
                .toBodilessEntity();

        DealStatus previous = deal.getStatus();
        deal.setStatus(DealStatus.FUNDED);
        deal.setFundedAt(LocalDateTime.now());
        dealRepository.save(deal);

        recordEvent(deal, "DEAL_FUNDED", userId, userRole, previous, DealStatus.FUNDED);
        dealEventProducer.publish("DEAL_FUNDED", deal);

        return DealResponse.from(deal);
    }

    @Transactional
    public DealResponse deliverDeal(UUID dealId, UUID userId, String userRole) {
        Deal deal = findDealOrThrow(dealId);
        if (!deal.getBeneficiaryId().equals(userId)) {
            throw new SecurityException("Only beneficiary can mark delivery");
        }
        DealStateMachine.validate(deal.getStatus(), DealStatus.DELIVERED);

        DealStatus previous = deal.getStatus();
        deal.setStatus(DealStatus.DELIVERED);
        deal.setDeliveredAt(LocalDateTime.now());
        dealRepository.save(deal);

        recordEvent(deal, "DEAL_DELIVERED", userId, userRole, previous, DealStatus.DELIVERED);
        dealEventProducer.publish("DEAL_DELIVERED", deal);

        return DealResponse.from(deal);
    }

    @Transactional
    public DealResponse confirmDeal(UUID dealId, UUID userId, String userRole) {
        Deal deal = findDealOrThrow(dealId);
        if (!deal.getDepositorId().equals(userId)) {
            throw new SecurityException("Only depositor can confirm release");
        }
        DealStateMachine.validate(deal.getStatus(), DealStatus.RELEASING);

        // Call payment-service to release funds
        paymentServiceClient.post()
                .uri("/internal/payments/release")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "dealId", deal.getId(),
                        "beneficiaryId", deal.getBeneficiaryId()
                ))
                .retrieve()
                .toBodilessEntity();

        DealStatus previous = deal.getStatus();
        deal.setStatus(DealStatus.COMPLETED);
        deal.setCompletedAt(LocalDateTime.now());
        dealRepository.save(deal);

        recordEvent(deal, "DEAL_COMPLETED", userId, userRole, previous, DealStatus.COMPLETED);
        dealEventProducer.publish("DEAL_COMPLETED", deal);

        return DealResponse.from(deal);
    }

    @Transactional(readOnly = true)
    public DealResponse getDeal(UUID dealId, UUID userId) {
        Deal deal = findDealOrThrow(dealId);
        checkAccess(deal, userId);
        return DealResponse.from(deal);
    }

    @Transactional(readOnly = true)
    public List<DealEventResponse> getDealEvents(UUID dealId, UUID userId) {
        Deal deal = findDealOrThrow(dealId);
        checkAccess(deal, userId);
        return dealEventRepository.findByDealIdOrderByCreatedAtAsc(dealId).stream()
                .map(DealEventResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DealResponse> listDeals(UUID userId) {
        return dealRepository.findByDepositorIdOrBeneficiaryIdOrderByCreatedAtDesc(userId, userId).stream()
                .map(DealResponse::from)
                .toList();
    }

    @Transactional
    public DealResponse cancelDeal(UUID dealId, UUID userId, String userRole) {
        Deal deal = findDealOrThrow(dealId);
        checkAccess(deal, userId);
        DealStateMachine.validate(deal.getStatus(), DealStatus.CANCELLED);

        DealStatus previous = deal.getStatus();
        deal.setStatus(DealStatus.CANCELLED);
        deal.setCancelledAt(LocalDateTime.now());
        dealRepository.save(deal);

        recordEvent(deal, "DEAL_CANCELLED", userId, userRole, previous, DealStatus.CANCELLED);
        dealEventProducer.publish("DEAL_CANCELLED", deal);

        return DealResponse.from(deal);
    }

    private Deal findDealOrThrow(UUID dealId) {
        return dealRepository.findById(dealId)
                .orElseThrow(() -> new IllegalArgumentException("Deal not found"));
    }

    private void checkAccess(Deal deal, UUID userId) {
        if (!deal.getDepositorId().equals(userId) && !deal.getBeneficiaryId().equals(userId)) {
            throw new SecurityException("Access denied");
        }
    }

    private void recordEvent(Deal deal, String eventType, UUID actorId, String actorRole,
                             DealStatus previousStatus, DealStatus newStatus) {
        DealEvent event = new DealEvent();
        event.setDeal(deal);
        event.setEventType(eventType);
        event.setActorId(actorId);
        event.setActorRole(actorRole);
        event.setPreviousStatus(previousStatus);
        event.setNewStatus(newStatus);
        dealEventRepository.save(event);
    }
}
