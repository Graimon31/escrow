package com.escrow.deal.statemachine;

import com.escrow.deal.entity.DealStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class DealStateMachine {

    private static final Map<DealStatus, Set<DealStatus>> TRANSITIONS;

    static {
        Map<DealStatus, Set<DealStatus>> t = new HashMap<>();
        t.put(DealStatus.DRAFT,                Set.of(DealStatus.AWAITING_AGREEMENT, DealStatus.CANCELLED));
        t.put(DealStatus.AWAITING_AGREEMENT,   Set.of(DealStatus.AGREED, DealStatus.CANCELLED));
        t.put(DealStatus.AGREED,               Set.of(DealStatus.AWAITING_FUNDING, DealStatus.CANCELLED));
        t.put(DealStatus.AWAITING_FUNDING,     Set.of(DealStatus.FUNDING_PROCESSING, DealStatus.CANCELLED));
        t.put(DealStatus.FUNDING_PROCESSING,   Set.of(DealStatus.FUNDED, DealStatus.AWAITING_FUNDING, DealStatus.CANCELLED));
        t.put(DealStatus.FUNDED,               Set.of(DealStatus.AWAITING_FULFILLMENT, DealStatus.CANCELLED));
        t.put(DealStatus.AWAITING_FULFILLMENT, Set.of(DealStatus.AWAITING_REVIEW, DealStatus.DISPUTED, DealStatus.CANCELLED));
        t.put(DealStatus.AWAITING_REVIEW,      Set.of(DealStatus.RELEASING, DealStatus.DISPUTED, DealStatus.REFUNDING, DealStatus.CANCELLED));
        t.put(DealStatus.RELEASING,            Set.of(DealStatus.COMPLETED, DealStatus.CANCELLED));
        t.put(DealStatus.REFUNDING,            Set.of(DealStatus.REFUNDED, DealStatus.CANCELLED));
        t.put(DealStatus.DISPUTED,             Set.of(DealStatus.RELEASING, DealStatus.REFUNDING, DealStatus.CLOSED));
        TRANSITIONS = Map.copyOf(t);
    }

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
