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
        deal.setStatus(DealStatus.DRAFT);
        dealRepository.save(deal);

        recordEvent(deal, "DEAL_CREATED", depositorId, "DEPOSITOR", null, DealStatus.DRAFT);
        dealEventProducer.publish("DEAL_CREATED", deal);

        return DealResponse.from(deal);
    }

    @Transactional
    public DealResponse submitDeal(UUID dealId, UUID userId, String userRole) {
        Deal deal = findDealOrThrow(dealId);
        if (!deal.getDepositorId().equals(userId)) {
            throw new SecurityException("Only depositor can submit the deal");
        }
        DealStateMachine.validate(deal.getStatus(), DealStatus.AWAITING_AGREEMENT);

        DealStatus previous = deal.getStatus();
        deal.setStatus(DealStatus.AWAITING_AGREEMENT);
        dealRepository.save(deal);

        recordEvent(deal, "DEAL_SUBMITTED", userId, userRole, previous, DealStatus.AWAITING_AGREEMENT);
        dealEventProducer.publish("DEAL_SUBMITTED", deal);

        return DealResponse.from(deal);
    }

    @Transactional
    public DealResponse agreeDeal(UUID dealId, UUID userId, String userRole) {
        Deal deal = findDealOrThrow(dealId);
        if (!deal.getBeneficiaryId().equals(userId)) {
            throw new SecurityException("Only beneficiary can agree to the deal");
        }
        DealStateMachine.validate(deal.getStatus(), DealStatus.AGREED);

        DealStatus previous = deal.getStatus();
        deal.setStatus(DealStatus.AGREED);
        deal.setAgreedAt(LocalDateTime.now());
        dealRepository.save(deal);

        // Auto-transition to AWAITING_FUNDING
        DealStateMachine.validate(deal.getStatus(), DealStatus.AWAITING_FUNDING);
        deal.setStatus(DealStatus.AWAITING_FUNDING);
        dealRepository.save(deal);

        recordEvent(deal, "DEAL_AGREED", userId, userRole, previous, DealStatus.AWAITING_FUNDING);
        dealEventProducer.publish("DEAL_AGREED", deal);

        return DealResponse.from(deal);
    }

    @Transactional
    public DealResponse declineDeal(UUID dealId, UUID userId, String userRole) {
        Deal deal = findDealOrThrow(dealId);
        if (!deal.getBeneficiaryId().equals(userId)) {
            throw new SecurityException("Only beneficiary can decline the deal");
        }
        DealStateMachine.validate(deal.getStatus(), DealStatus.CANCELLED);

        DealStatus previous = deal.getStatus();
        deal.setStatus(DealStatus.CANCELLED);
        deal.setCancelledAt(LocalDateTime.now());
        dealRepository.save(deal);

        recordEvent(deal, "DEAL_DECLINED", userId, userRole, previous, DealStatus.CANCELLED);
        dealEventProducer.publish("DEAL_DECLINED", deal);

        return DealResponse.from(deal);
    }

    @Transactional
    public DealResponse fundDeal(UUID dealId, UUID userId, String userRole) {
        Deal deal = findDealOrThrow(dealId);
        if (!deal.getDepositorId().equals(userId)) {
            throw new SecurityException("Only depositor can fund the deal");
        }
        DealStateMachine.validate(deal.getStatus(), DealStatus.FUNDING_PROCESSING);

        DealStatus previous = deal.getStatus();
        deal.setStatus(DealStatus.FUNDING_PROCESSING);
        dealRepository.save(deal);

        // Call payment-service to hold funds
        try {
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
        } catch (Exception e) {
            // Revert to AWAITING_FUNDING on payment failure
            deal.setStatus(DealStatus.AWAITING_FUNDING);
            dealRepository.save(deal);
            throw new RuntimeException("Payment failed: " + e.getMessage(), e);
        }

        deal.setStatus(DealStatus.FUNDED);
        deal.setFundedAt(LocalDateTime.now());
        dealRepository.save(deal);

        // Auto-transition to AWAITING_FULFILLMENT
        deal.setStatus(DealStatus.AWAITING_FULFILLMENT);
        dealRepository.save(deal);

        recordEvent(deal, "DEAL_FUNDED", userId, userRole, previous, DealStatus.AWAITING_FULFILLMENT);
        dealEventProducer.publish("DEAL_FUNDED", deal);

        return DealResponse.from(deal);
    }

    @Transactional
    public DealResponse deliverDeal(UUID dealId, UUID userId, String userRole) {
        Deal deal = findDealOrThrow(dealId);
        if (!deal.getBeneficiaryId().equals(userId)) {
            throw new SecurityException("Only beneficiary can mark delivery");
        }
        DealStateMachine.validate(deal.getStatus(), DealStatus.AWAITING_REVIEW);

        DealStatus previous = deal.getStatus();
        deal.setStatus(DealStatus.AWAITING_REVIEW);
        deal.setDeliveredAt(LocalDateTime.now());
        dealRepository.save(deal);

        recordEvent(deal, "DEAL_DELIVERED", userId, userRole, previous, DealStatus.AWAITING_REVIEW);
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

        DealStatus previous = deal.getStatus();
        deal.setStatus(DealStatus.RELEASING);
        dealRepository.save(deal);

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

        deal.setStatus(DealStatus.COMPLETED);
        deal.setCompletedAt(LocalDateTime.now());
        dealRepository.save(deal);

        recordEvent(deal, "DEAL_COMPLETED", userId, userRole, previous, DealStatus.COMPLETED);
        dealEventProducer.publish("DEAL_COMPLETED", deal);

        return DealResponse.from(deal);
    }

    @Transactional
    public DealResponse rejectDeal(UUID dealId, UUID userId, String userRole) {
        Deal deal = findDealOrThrow(dealId);
        if (!deal.getDepositorId().equals(userId)) {
            throw new SecurityException("Only depositor can reject and request refund");
        }
        DealStateMachine.validate(deal.getStatus(), DealStatus.REFUNDING);

        DealStatus previous = deal.getStatus();
        deal.setStatus(DealStatus.REFUNDING);
        dealRepository.save(deal);

        // Call payment-service to refund
        paymentServiceClient.post()
                .uri("/internal/payments/refund")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "dealId", deal.getId(),
                        "depositorId", deal.getDepositorId()
                ))
                .retrieve()
                .toBodilessEntity();

        deal.setStatus(DealStatus.REFUNDED);
        dealRepository.save(deal);

        recordEvent(deal, "DEAL_REFUNDED", userId, userRole, previous, DealStatus.REFUNDED);
        dealEventProducer.publish("DEAL_REFUNDED", deal);

        return DealResponse.from(deal);
    }

    @Transactional
    public DealResponse disputeDeal(UUID dealId, UUID userId, String userRole) {
        Deal deal = findDealOrThrow(dealId);
        checkAccess(deal, userId);
        DealStateMachine.validate(deal.getStatus(), DealStatus.DISPUTED);

        DealStatus previous = deal.getStatus();
        deal.setStatus(DealStatus.DISPUTED);
        deal.setDisputedAt(LocalDateTime.now());
        dealRepository.save(deal);

        recordEvent(deal, "DEAL_DISPUTED", userId, userRole, previous, DealStatus.DISPUTED);
        dealEventProducer.publish("DEAL_DISPUTED", deal);

        return DealResponse.from(deal);
    }

    @Transactional
    public DealResponse resolveDispute(UUID dealId, UUID userId, String userRole, String resolution) {
        Deal deal = findDealOrThrow(dealId);
        // Only operator/admin can resolve disputes
        if (!"OPERATOR".equals(userRole) && !"ADMINISTRATOR".equals(userRole)) {
            throw new SecurityException("Only operator or administrator can resolve disputes");
        }

        if ("RELEASE".equalsIgnoreCase(resolution)) {
            DealStateMachine.validate(deal.getStatus(), DealStatus.RELEASING);
            deal.setStatus(DealStatus.RELEASING);
            dealRepository.save(deal);

            paymentServiceClient.post()
                    .uri("/internal/payments/release")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("dealId", deal.getId(), "beneficiaryId", deal.getBeneficiaryId()))
                    .retrieve()
                    .toBodilessEntity();

            DealStatus previous = DealStatus.DISPUTED;
            deal.setStatus(DealStatus.COMPLETED);
            deal.setCompletedAt(LocalDateTime.now());
            deal.setClosedAt(LocalDateTime.now());
            dealRepository.save(deal);

            recordEvent(deal, "DISPUTE_RESOLVED_RELEASE", userId, userRole, previous, DealStatus.COMPLETED);
            dealEventProducer.publish("DEAL_COMPLETED", deal);
        } else if ("REFUND".equalsIgnoreCase(resolution)) {
            DealStateMachine.validate(deal.getStatus(), DealStatus.REFUNDING);
            deal.setStatus(DealStatus.REFUNDING);
            dealRepository.save(deal);

            paymentServiceClient.post()
                    .uri("/internal/payments/refund")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("dealId", deal.getId(), "depositorId", deal.getDepositorId()))
                    .retrieve()
                    .toBodilessEntity();

            DealStatus previous = DealStatus.DISPUTED;
            deal.setStatus(DealStatus.REFUNDED);
            deal.setClosedAt(LocalDateTime.now());
            dealRepository.save(deal);

            recordEvent(deal, "DISPUTE_RESOLVED_REFUND", userId, userRole, previous, DealStatus.REFUNDED);
            dealEventProducer.publish("DEAL_REFUNDED", deal);
        } else {
            throw new IllegalArgumentException("Resolution must be RELEASE or REFUND");
        }

        return DealResponse.from(deal);
    }

    @Transactional(readOnly = true)
    public DealResponse getDeal(UUID dealId, UUID userId) {
        return getDeal(dealId, userId, null);
    }

    @Transactional(readOnly = true)
    public DealResponse getDeal(UUID dealId, UUID userId, String userRole) {
        Deal deal = findDealOrThrow(dealId);
        if (!isOperatorOrAdmin(userRole)) {
            checkAccess(deal, userId);
        }
        return DealResponse.from(deal);
    }

    @Transactional(readOnly = true)
    public List<DealEventResponse> getDealEvents(UUID dealId, UUID userId) {
        return getDealEvents(dealId, userId, null);
    }

    @Transactional(readOnly = true)
    public List<DealEventResponse> getDealEvents(UUID dealId, UUID userId, String userRole) {
        Deal deal = findDealOrThrow(dealId);
        if (!isOperatorOrAdmin(userRole)) {
            checkAccess(deal, userId);
        }
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

    private boolean isOperatorOrAdmin(String role) {
        return "OPERATOR".equals(role) || "ADMINISTRATOR".equals(role);
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
