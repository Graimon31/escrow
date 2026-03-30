package com.escrow.deal.entity;

public enum DealStatus {
    DRAFT,
    AWAITING_AGREEMENT,
    AGREED,
    AWAITING_FUNDING,
    FUNDING_PROCESSING,
    FUNDED,
    AWAITING_FULFILLMENT,
    AWAITING_REVIEW,
    RELEASING,
    COMPLETED,
    REFUNDING,
    REFUNDED,
    DISPUTED,
    CANCELLED,
    CLOSED;

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == REFUNDED || this == CLOSED;
    }
}
