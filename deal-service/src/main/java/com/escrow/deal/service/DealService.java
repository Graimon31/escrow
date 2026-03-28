package com.escrow.deal.service;

import com.escrow.deal.dto.CreateDealRequest;
import com.escrow.deal.dto.DealEventResponse;
import com.escrow.deal.dto.DealResponse;
import com.escrow.deal.entity.Deal;
import com.escrow.deal.entity.DealEvent;
import com.escrow.deal.entity.DealStatus;
import com.escrow.deal.repository.DealEventRepository;
import com.escrow.deal.repository.DealRepository;
import com.escrow.deal.statemachine.DealStateMachine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DealService {

    private final DealRepository dealRepository;
    private final DealEventRepository dealEventRepository;

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
