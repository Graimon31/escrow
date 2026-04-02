package com.escrow.payment.entity;

/**
 * Escrow Account State Machine (parallel to Deal State Machine):
 *
 * NOT_CREATED → OPENED → FUNDS_DEPOSITING → FUNDS_SECURED
 *   → RELEASING → RELEASED_TO_BENEFICIARY (terminal)
 *   → REFUNDING → REFUNDED_TO_DEPOSITOR (terminal)
 *   → DISPUTED
 *     → RELEASING → RELEASED_TO_BENEFICIARY
 *     → REFUNDING → REFUNDED_TO_DEPOSITOR
 *   → CANCELLED (terminal)
 */
public enum EscrowAccountStatus {
    NOT_CREATED,
    OPENED,
    FUNDS_DEPOSITING,
    FUNDS_SECURED,
    RELEASING,
    RELEASED_TO_BENEFICIARY,
    REFUNDING,
    REFUNDED_TO_DEPOSITOR,
    DISPUTED,
    CANCELLED;

    public boolean isTerminal() {
        return this == RELEASED_TO_BENEFICIARY || this == REFUNDED_TO_DEPOSITOR || this == CANCELLED;
    }
}
