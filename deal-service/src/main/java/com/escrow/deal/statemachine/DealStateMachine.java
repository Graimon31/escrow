package com.escrow.deal.statemachine;

import com.escrow.deal.entity.DealStatus;

import java.util.Map;
import java.util.Set;

public final class DealStateMachine {

    private static final Map<DealStatus, Set<DealStatus>> TRANSITIONS = Map.of(
            DealStatus.CREATED,    Set.of(DealStatus.FUNDED, DealStatus.CANCELLED),
            DealStatus.FUNDED,     Set.of(DealStatus.DELIVERED, DealStatus.CANCELLED),
            DealStatus.DELIVERED,  Set.of(DealStatus.RELEASING, DealStatus.DISPUTED, DealStatus.CANCELLED),
            DealStatus.RELEASING,  Set.of(DealStatus.COMPLETED, DealStatus.CANCELLED),
            DealStatus.DISPUTED,   Set.of(DealStatus.RESOLVED, DealStatus.CANCELLED)
    );

    private DealStateMachine() {}

    public static boolean canTransition(DealStatus from, DealStatus to) {
        Set<DealStatus> allowed = TRANSITIONS.get(from);
        return allowed != null && allowed.contains(to);
    }

    public static void validate(DealStatus from, DealStatus to) {
        if (!canTransition(from, to)) {
            throw new IllegalStateException(
                    "Cannot transition from " + from + " to " + to);
        }
    }
}
