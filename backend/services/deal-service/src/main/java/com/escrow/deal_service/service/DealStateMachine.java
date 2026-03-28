package com.escrow.deal_service.service;

import com.escrow.deal_service.domain.DealState;

public final class DealStateMachine {
    private DealStateMachine() {}

    public static DealState agree(DealState current) {
        if (current != DealState.DRAFT) {
            throw new IllegalStateException("Сделку можно согласовать только из состояния DRAFT");
        }
        return DealState.AGREED;
    }

    public static DealState openAccount(DealState current) {
        if (current != DealState.AGREED) {
            throw new IllegalStateException("Счёт эскроу можно открыть только из состояния AGREED");
        }
        return DealState.ACCOUNT_OPENED;
    }

    public static DealState awaitFunding(DealState current) {
        if (current != DealState.ACCOUNT_OPENED) {
            throw new IllegalStateException("Ожидание фондирования возможно только после ACCOUNT_OPENED");
        }
        return DealState.AWAITING_FUNDING;
    }
}
