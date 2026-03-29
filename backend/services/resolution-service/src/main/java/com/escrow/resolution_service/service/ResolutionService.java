package com.escrow.resolution_service.service;

import com.escrow.resolution_service.domain.ResolutionDecision;
import com.escrow.resolution_service.domain.ResolutionDecisionRepository;
import com.escrow.resolution_service.domain.ResolutionOutcome;
import com.escrow.resolution_service.event.ResolutionEvent;
import com.escrow.resolution_service.event.ResolutionEventPublisher;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ResolutionService {

    private final ResolutionDecisionRepository repository;
    private final ResolutionEventPublisher publisher;

    public ResolutionService(ResolutionDecisionRepository repository, ResolutionEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public ResolutionDecision resolve(UUID dealId, ResolutionOutcome outcome, String actor, String comment) {
        ResolutionDecision decision = new ResolutionDecision();
        decision.setDealId(dealId);
        decision.setOutcome(outcome);
        decision.setActor(actor);
        decision.setComment(comment);
        decision = repository.save(decision);

        String eventType = switch (outcome) {
            case RELEASE -> "FUNDS_RELEASED";
            case REFUND -> "FUNDS_REFUNDED";
        };
        publisher.publish(new ResolutionEvent(dealId, eventType, actor, comment, OffsetDateTime.now()));

        return decision;
    }

    public List<ResolutionDecision> history(UUID dealId) {
        return repository.findByDealIdOrderByCreatedAtDesc(dealId);
    }
}
