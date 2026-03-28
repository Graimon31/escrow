package com.escrow.deal.entity;

public enum DealStatus {
    CREATED,
    FUNDED,
    DELIVERED,
    RELEASING,
    COMPLETED,
    CANCELLED,
    DISPUTED,
    RESOLVED;

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == RESOLVED;
    }
}
