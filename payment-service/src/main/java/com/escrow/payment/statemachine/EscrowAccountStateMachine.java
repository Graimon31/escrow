package com.escrow.payment.statemachine;

import com.escrow.payment.entity.EscrowAccountStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EscrowAccountStateMachine {

    private static final Map<EscrowAccountStatus, Set<EscrowAccountStatus>> TRANSITIONS;

    static {
        Map<EscrowAccountStatus, Set<EscrowAccountStatus>> t = new HashMap<>();
        t.put(EscrowAccountStatus.NOT_CREATED, Set.of(
                EscrowAccountStatus.OPENED, EscrowAccountStatus.CANCELLED));
        t.put(EscrowAccountStatus.OPENED, Set.of(
                EscrowAccountStatus.FUNDS_DEPOSITING, EscrowAccountStatus.CANCELLED));
        t.put(EscrowAccountStatus.FUNDS_DEPOSITING, Set.of(
                EscrowAccountStatus.FUNDS_SECURED, EscrowAccountStatus.OPENED, EscrowAccountStatus.CANCELLED));
        t.put(EscrowAccountStatus.FUNDS_SECURED, Set.of(
                EscrowAccountStatus.RELEASING, EscrowAccountStatus.REFUNDING, EscrowAccountStatus.DISPUTED));
        t.put(EscrowAccountStatus.DISPUTED, Set.of(
                EscrowAccountStatus.RELEASING, EscrowAccountStatus.REFUNDING));
        t.put(EscrowAccountStatus.RELEASING, Set.of(
                EscrowAccountStatus.RELEASED_TO_BENEFICIARY));
        t.put(EscrowAccountStatus.REFUNDING, Set.of(
                EscrowAccountStatus.REFUNDED_TO_DEPOSITOR));
        TRANSITIONS = Map.copyOf(t);
    }

    public static void validate(EscrowAccountStatus current, EscrowAccountStatus target) {
        Set<EscrowAccountStatus> allowed = TRANSITIONS.get(current);
        if (allowed == null || !allowed.contains(target)) {
            throw new IllegalStateException(
                    "Invalid escrow account transition: " + current + " → " + target);
        }
    }

    public static Set<EscrowAccountStatus> getAllowedTransitions(EscrowAccountStatus current) {
        return TRANSITIONS.getOrDefault(current, Set.of());
    }
}
