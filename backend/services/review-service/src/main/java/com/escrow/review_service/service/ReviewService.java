package com.escrow.review_service.service;

import com.escrow.review_service.domain.ReviewAction;
import com.escrow.review_service.domain.ReviewActionRepository;
import com.escrow.review_service.domain.ReviewActionType;
import com.escrow.review_service.event.ReviewEvent;
import com.escrow.review_service.event.ReviewEventPublisher;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewActionRepository repository;
    private final ReviewEventPublisher publisher;

    public ReviewService(ReviewActionRepository repository, ReviewEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public ReviewAction act(UUID dealId, ReviewActionType actionType, String actor, String comment) {
        ReviewAction action = new ReviewAction();
        action.setDealId(dealId);
        action.setActionType(actionType);
        action.setActor(actor);
        action.setComment(comment);
        action = repository.save(action);

        String eventType = switch (actionType) {
            case ACCEPT -> "REVIEW_ACCEPTED";
            case REJECT -> "REVIEW_REJECTED";
            case CORRECTION -> "CORRECTION_REQUESTED";
            case DISPUTE -> "DISPUTE_OPENED";
        };
        publisher.publish(new ReviewEvent(dealId, eventType, actor, comment, OffsetDateTime.now()));

        return action;
    }

    public List<ReviewAction> history(UUID dealId) {
        return repository.findByDealIdOrderByCreatedAtAsc(dealId);
    }
}
