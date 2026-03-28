package com.escrow.dispute_service.service;

import com.escrow.dispute_service.domain.DisputeCase;
import com.escrow.dispute_service.domain.DisputeCaseRepository;
import com.escrow.dispute_service.domain.DisputeStatus;
import com.escrow.dispute_service.event.DisputeEvent;
import com.escrow.dispute_service.event.DisputeEventPublisher;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DisputeService {

    private final DisputeCaseRepository repository;
    private final DisputeEventPublisher publisher;

    public DisputeService(DisputeCaseRepository repository, DisputeEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public DisputeCase open(UUID dealId, String actor, String reason) {
        repository.findFirstByDealIdAndStatusOrderByCreatedAtDesc(dealId, DisputeStatus.OPEN)
                .ifPresent(existing -> { throw new IllegalStateException("По сделке уже открыт спор"); });

        DisputeCase dispute = new DisputeCase();
        dispute.setDealId(dealId);
        dispute.setOpenedBy(actor);
        dispute.setReason(reason);
        dispute.setStatus(DisputeStatus.OPEN);
        dispute = repository.save(dispute);

        publisher.publish(new DisputeEvent(dealId, "DISPUTE_OPENED", actor, reason, OffsetDateTime.now()));
        return dispute;
    }

    public void markResolved(UUID dealId, String actor, String resolutionComment) {
        repository.findFirstByDealIdAndStatusOrderByCreatedAtDesc(dealId, DisputeStatus.OPEN).ifPresent(dispute -> {
            dispute.setStatus(DisputeStatus.RESOLVED);
            dispute.setResolvedBy(actor);
            dispute.setResolutionComment(resolutionComment);
            dispute.setResolvedAt(OffsetDateTime.now());
            repository.save(dispute);
        });
    }

    public List<DisputeCase> history(UUID dealId) {
        return repository.findByDealIdOrderByCreatedAtDesc(dealId);
    }
}
