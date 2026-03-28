package com.escrow.escrow_account_service.domain;

public enum EscrowAccountState {
    OPENED,
    AWAITING_DEPOSIT,
    DEPOSIT_IN_PROCESS,
    HELD_IN_ESCROW,
    RELEASED_TO_BENEFICIARY,
    REFUNDED_TO_DEPOSITOR,
    CLOSED
}
