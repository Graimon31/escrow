package com.escrow.deal.dto;

import com.escrow.deal.entity.DealEvent;
import com.escrow.deal.entity.DealStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class DealEventResponse {
    private UUID id;
    private String eventType;
    private UUID actorId;
    private String actorRole;
    private DealStatus previousStatus;
    private DealStatus newStatus;
    private LocalDateTime createdAt;

    public static DealEventResponse from(DealEvent event) {
        return new DealEventResponse(
                event.getId(),
                event.getEventType(),
                event.getActorId(),
                event.getActorRole(),
                event.getPreviousStatus(),
                event.getNewStatus(),
                event.getCreatedAt()
        );
    }
}
